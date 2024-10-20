package com.example.skycast.Home.ViewModel

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

    fun fetchWeatherByCity(city: String, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.value = Result.Loading

            val result = weatherRepository.getWeatherByCity(city, apiKey)

            _weatherData.value = result
        }
    }


    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.value = Result.Loading

            val result = weatherRepository.getWeatherByCoordinates(lat, lon, apiKey)

            _weatherData.value = result
        }
    }


}