package com.example.skycast.Home.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.model.CurrentResponseApi
import com.github.matteobattilana.weather.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.skycast.network.Result


class HomeViewModel (private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<Result<CurrentResponseApi>>(Result.Loading)
    val weatherData: StateFlow<Result<CurrentResponseApi>> get() = _weatherData
    private var isWeatherFetched = false
    private var lastLocation: String? = null

    fun fetchWeatherByCity(city: String, apiKey: String) {
//        isWeatherFetched = false

        if (isWeatherFetched) return

        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.value = Result.Loading

            val result = weatherRepository.getWeatherByCity(city, apiKey, "metric")

            Log.d("HomeViewModel", "Weather response: $result")

            _weatherData.emit(result)
            isWeatherFetched = true
        }
    }

    fun fetchWeatherData(location: String) {
        if (location != lastLocation) {
            lastLocation = location
            fetchWeatherByCity(location, "85f1176e73af023bdc219b8e180d44d6")
        } else {
            Log.d("WeatherViewModel", "Same location requested: $location. Skipping API call.")
        }
    }

    fun onLocationChanged() {
        isWeatherFetched = false
    }


    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.value = Result.Loading

            val result = weatherRepository.getWeatherByCoordinates(lat, lon, apiKey, "metric")

            _weatherData.value = result
        }
    }


}