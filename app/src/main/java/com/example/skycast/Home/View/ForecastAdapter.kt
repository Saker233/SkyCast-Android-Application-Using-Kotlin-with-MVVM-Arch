package com.example.skycast.Home.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.Settings.SettingsManager
import com.example.skycast.model.FiveDaysResponseApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherAdapter(
    private var weatherList: List<FiveDaysResponseApi.data>,
    private var temperatureUnit: String,
    private val settingsManager: SettingsManager // Pass SettingsManager to access the language setting
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTxt: TextView = itemView.findViewById(R.id.dayTxt)
        val hourTxt: TextView = itemView.findViewById(R.id.hourTxt)
        val pic: ImageView = itemView.findViewById(R.id.pic)
        val tempTxt: TextView = itemView.findViewById(R.id.tempTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_forecast, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherData = weatherList[position]

        // Determine the correct locale based on the language setting
        val locale = if (settingsManager.getLanguage() == SettingsManager.LANGUAGE_ARABIC) {
            Locale("ar")
        } else {
            Locale.getDefault()
        }

        val dateTime = weatherData.dtTxt ?: "Unknown Date"
        val dateParts = dateTime.split(" ")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date? = dateFormat.parse(dateParts[0])

        // Use Arabic locale for day formatting if Arabic is selected
        val dayFormat = SimpleDateFormat("EEE", locale)
        holder.dayTxt.text = date?.let { dayFormat.format(it) } ?: "Unknown Day"

        val timeParts = dateParts.getOrNull(1)?.split(":") ?: listOf("00", "00")
        val hourFormat = SimpleDateFormat("h:mm a", locale)
        val hour = SimpleDateFormat("HH:mm", locale).parse("${timeParts[0]}:${timeParts[1]}")

        holder.hourTxt.text = hour?.let { hourFormat.format(it) } ?: "Unknown Hour"

        val temperature = weatherData.main?.temp ?: 0.0
        holder.tempTxt.text = "${Math.round(temperature)}°${getUnitSymbol()}"

        // Set the weather icon based on the icon code
        val iconCode = weatherData.weather?.get(0)?.icon ?: "01d"
        val iconResId = getIconResource(iconCode)
        holder.pic.setImageResource(iconResId)
    }

    override fun getItemCount(): Int = weatherList.size

    private fun getIconResource(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.sunny
            "02d" -> R.drawable.cloudy
            "09d" -> R.drawable.rainy
            "13d" -> R.drawable.snowy
            else -> R.drawable.sunny
        }
    }

    private fun getUnitSymbol(): String {
        return when (temperatureUnit) {
            "Celsius" -> "C"
            "Fahrenheit" -> "F"
            else -> "K"
        }
    }

    fun updateTemperatureUnit(newUnit: String) {
        temperatureUnit = newUnit
        notifyDataSetChanged()
    }

    fun updateWeatherList(newWeatherList: List<FiveDaysResponseApi.data>) {
        weatherList = newWeatherList
        notifyDataSetChanged()
    }
}
