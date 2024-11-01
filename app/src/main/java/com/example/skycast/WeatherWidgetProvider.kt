package com.example.skycast

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.skycast.R
import com.example.skycast.Settings.SettingsManager
import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.SharedPreferencesHelper

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)


        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val weatherData: CurrentResponseApi? = sharedPreferencesHelper.getWeatherData()

        if (weatherData != null) {
            val cityName = weatherData.name ?: "Unknown City"
            views.setTextViewText(R.id.widgetCityName, cityName)

            val temperatureUnit = getTemperatureUnitSymbol(context)
            val temperature = weatherData.main?.temp?.let { Math.round(it) } ?: 0
            views.setTextViewText(R.id.widgetTemperature, "$temperature$temperatureUnit")

            val weatherDescription = weatherData.weather?.get(0)?.description ?: "N/A"
            views.setTextViewText(R.id.widgetStatus, weatherDescription.capitalize())

            val windSpeed = weatherData.wind?.speed?.let { Math.round(it) } ?: 0
            val windSpeedUnit = if (temperatureUnit == "°F") "mph" else "m/s"
            views.setTextViewText(R.id.widgetWindSpeed, "Wind: $windSpeed $windSpeedUnit")

            val humidity = weatherData.main?.humidity ?: 0
            views.setTextViewText(R.id.widgetHumidity, "Humidity: $humidity%")

            val icon = weatherData.weather?.get(0)?.icon ?: "01d"
            val iconResId = getIconResource(icon)
            views.setImageViewResource(R.id.widgetWeatherIcon, iconResId)
        } else {
            views.setTextViewText(R.id.widgetCityName, "N/A")
            views.setTextViewText(R.id.widgetTemperature, "N/A")
            views.setTextViewText(R.id.widgetStatus, "N/A")
            views.setTextViewText(R.id.widgetWindSpeed, "Wind: N/A")
            views.setTextViewText(R.id.widgetHumidity, "Humidity: N/A")
            views.setImageViewResource(R.id.widgetWeatherIcon, R.drawable.sunny)
        }

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun getTemperatureUnitSymbol(context: Context): String {
        val settingsManager = SettingsManager(context)
        return when (settingsManager.getTemperatureUnit()) {
            SettingsManager.UNIT_CELSIUS -> "°C"
            SettingsManager.UNIT_FAHRENHEIT -> "°F"
            SettingsManager.UNIT_KELVIN -> "K"
            else -> "°C"
        }
    }

    private fun getIconResource(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01" -> R.drawable.sunny
            "02" -> R.drawable.cloudy
            "03", "04" -> R.drawable.cloudy
            "09", "10" -> R.drawable.rainy
            "11" -> R.drawable.storm
            "13" -> R.drawable.snowy
            "50" -> R.drawable.cloudy_sunny
            else -> R.drawable.sunny
        }
    }
}
