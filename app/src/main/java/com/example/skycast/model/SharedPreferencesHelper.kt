package com.example.skycast.model

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.example.skycast.model.CurrentResponseApi

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()

    companion object {
        private const val WEATHER_DATA_KEY = "weather_data_key"
    }

    fun saveWeatherData(weatherData: CurrentResponseApi) {
        val json = gson.toJson(weatherData)
        sharedPreferences.edit().putString(WEATHER_DATA_KEY, json).apply()
    }

    fun getWeatherData(): CurrentResponseApi? {
        val json = sharedPreferences.getString(WEATHER_DATA_KEY, null)
        return gson.fromJson(json, CurrentResponseApi::class.java)
    }
}
