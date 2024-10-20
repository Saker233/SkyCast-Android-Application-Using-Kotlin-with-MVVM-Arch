package com.example.skycast.Repo

import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.skycast.network.Result

class WeatherRepository(
    private val weatherApiService: ApiService
) {

    suspend fun getWeatherByCity(city: String, apiKey: String, units: String): Result<CurrentResponseApi> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getWeatherByCity(city, apiKey, units)
                handleResponse(response)
            } catch (e: Exception) {
                Result.Failure("Exception: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getWeatherByCoordinates(lat: Double, lon: Double, apiKey: String, units: String): Result<CurrentResponseApi> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getWeatherByCoordinates(lat, lon, apiKey, units)
                handleResponse(response)
            } catch (e: Exception) {
                Result.Failure("Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun <T> handleResponse(response: retrofit2.Response<T>): Result<T> {
        return if (response.isSuccessful && response.body() != null) {
            Result.Success(response.body()!!)
        } else {
            Result.Failure("Failed to fetch data: ${response.message()}")
        }
    }
}
