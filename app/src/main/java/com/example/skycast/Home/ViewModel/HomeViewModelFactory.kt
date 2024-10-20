package com.example.skycast.Home.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.Repo.WeatherRepository

class HomeViewModelFactory(private val weatherRepository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}