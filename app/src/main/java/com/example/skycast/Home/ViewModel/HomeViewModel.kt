package com.example.skycast.Home.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.FiveDaysResponseApi
import com.github.matteobattilana.weather.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.skycast.network.Result


class HomeViewModel (private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<Result<CurrentResponseApi>>(Result.Loading)
    val weatherData: StateFlow<Result<CurrentResponseApi>> get() = _weatherData

    private val _forecastData = MutableStateFlow<Result<FiveDaysResponseApi>>(Result.Loading)
    val forecastData: StateFlow<Result<FiveDaysResponseApi>> get() = _forecastData


    private var isWeatherFetched = false
    private var isForecastFetched = false
    private var lastLocation: String? = null



    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        if (isWeatherFetched) return
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.value = Result.Loading

            val result = weatherRepository.getWeatherByCoordinates(lat, lon, apiKey, "metric")

            _weatherData.emit(result)
            isWeatherFetched = true
        }
    }


    fun fetchFiveDayWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        if (isForecastFetched) return
        viewModelScope.launch(Dispatchers.IO) {
            _forecastData.value = Result.Loading
            val result = weatherRepository.getFiveDayWeatherByCoordinates(lat, lon, apiKey, "metric")
            Log.d("HomeViewModel", "Forecast Result: $result")
            _forecastData.emit(result)
            isForecastFetched = true
        }
    }




}