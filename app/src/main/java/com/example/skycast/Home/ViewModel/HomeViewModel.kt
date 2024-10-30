package com.example.skycast.Home.ViewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.Settings.SettingsManager
import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.FiveDaysResponseApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.skycast.network.Result

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsManager: SettingsManager // Use SettingsManager here
) : ViewModel() {

    private val _weatherData = MutableStateFlow<Result<CurrentResponseApi>>(Result.Loading)
    val weatherData: StateFlow<Result<CurrentResponseApi>> get() = _weatherData

    private val _forecastData = MutableStateFlow<Result<FiveDaysResponseApi>>(Result.Loading)
    val forecastData: StateFlow<Result<FiveDaysResponseApi>> get() = _forecastData

    // Listener to log and update on preference change
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SettingsManager.KEY_TEMPERATURE_UNIT || key == SettingsManager.KEY_LANGUAGE) {
                Log.d("HomeViewModel", "Preference changed: $key = ${settingsManager.getTemperatureUnit()}")
            }
        }

    init {
        settingsManager.preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    // Fetch units and language from SettingsManager
    private fun getUnits(): String = settingsManager.getTemperatureUnit()
    private fun getLanguage(): String = settingsManager.getLanguage()

    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.value = Result.Loading

            // Set the appropriate units parameter based on the user's choice
            val units = when (settingsManager.getTemperatureUnit()) {
                SettingsManager.UNIT_CELSIUS -> "metric"
                SettingsManager.UNIT_FAHRENHEIT -> "imperial"
                else -> null // Omit units parameter for Kelvin (default)
            }

            // Make the API call with or without units parameter based on the selection
            val result = if (units != null) {
                weatherRepository.getWeatherByCoordinates(lat, lon, apiKey, units, getLanguage())
            } else {
                weatherRepository.getWeatherByCoordinates(lat, lon, apiKey,
                    null.toString(), getLanguage())
            }

            Log.d("HomeViewModel", "Weather Result: $result")
            _weatherData.emit(result)
        }
    }


    fun fetchFiveDayWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _forecastData.value = Result.Loading
            val result = weatherRepository.getFiveDayWeatherByCoordinates(lat, lon, apiKey, getUnits(), getLanguage())
            Log.d("HomeViewModel", "Forecast Result: $result")
            _forecastData.emit(result)
        }
    }

    override fun onCleared() {
        super.onCleared()
        settingsManager.preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }
}
