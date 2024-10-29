package com.example.skycast.Alert.View

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.skycast.Alert.Model.AlertDatabase
import com.example.skycast.Alert.Model.AlertRepository
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.db.FavoritePlacesDatabase
import com.example.skycast.network.RetrofitHelper
import com.example.skycast.model.CurrentResponseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.skycast.network.Result

class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlertReceiver", "Received alarm broadcast")

        val title = intent.getStringExtra("title") ?: "Weather Alert"
        val duration = intent.getLongExtra("duration", 10)
        val alertId = intent.getLongExtra("alertId", -1)

        val latitude = 30.0711
        val longitude = 31.0211
        val apiKey = intent.getStringExtra("apiKey") ?: ""

        if (alertId != -1L) {
            fetchWeatherAndNotify(context, title, duration, latitude, longitude, apiKey, alertId.toInt())
            deleteAlertFromDatabase(context, alertId)
        }
    }

    private fun fetchWeatherAndNotify(
        context: Context,
        title: String,
        duration: Long,
        lat: Double,
        lon: Double,
        apiKey: String,
        alertId: Int
    ) {
        val placeDao = FavoritePlacesDatabase.getDatabase(context).placeDao()
        val weatherRepository = WeatherRepository(RetrofitHelper.service, placeDao)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val weatherResult = weatherRepository.getWeatherByCoordinates(lat, lon, apiKey, "metric")
                val message = if (weatherResult is Result.Success) {
                    buildWeatherMessage(weatherResult.data, duration)
                } else {
                    "Weather alert for the next $duration minutes"
                }
                withContext(Dispatchers.Main) {
                    sendNotification(context, title, message, alertId)
                }
            } catch (e: Exception) {
                Log.e("AlertReceiver", "Failed to fetch weather data", e)
                withContext(Dispatchers.Main) {
                    sendNotification(context, title, "Unable to retrieve weather data", alertId)
                }
            }
        }
    }

    private fun buildWeatherMessage(weatherData: CurrentResponseApi, duration: Long): String {
        val temperature = weatherData.main?.temp ?: 0.0
        val condition = weatherData.weather?.get(0)?.description ?: "Unknown"
        return "Current temperature is ${temperature}°C with ${condition}."
    }

    private fun sendNotification(context: Context, title: String, message: String, alertId: Int) {
        val channelId = "weather_alert_channel"
        val channelName = "Weather Alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(alertId, notificationBuilder.build())
    }

    private fun deleteAlertFromDatabase(context: Context, alertId: Long) {
        val alertDao = AlertDatabase.getDatabase(context).alertDao()
        val repository = AlertRepository(alertDao)

        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteAlertByAlertId(alertId)
            Log.d("AlertReceiver", "Alert with ID $alertId deleted after notification.")
        }
    }
}
