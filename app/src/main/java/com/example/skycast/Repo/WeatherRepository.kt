package com.example.skycast.Repo

import android.util.Log
import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.FiveDaysResponseApi
import com.example.skycast.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.skycast.network.Result

class WeatherRepository(
    private val weatherApiService: ApiService
) {


    suspend fun getWeatherByCoordinates(lat: Double, lon: Double, apiKey: String, units: String): Result<CurrentResponseApi> {
        return try {
            val response = weatherApiService.getWeatherByCoordinates(lat, lon, apiKey, units)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Failure("Error fetching weather data")
            }
        } catch (e: Exception) {
            Result.Failure("Network error: ${e.message}")
        }
    }


    suspend fun getFiveDayWeatherByCoordinates(lat: Double, lon: Double, apiKey: String, units: String): Result<FiveDaysResponseApi> {
        return try {
            val response = weatherApiService.getFiveDayWeatherByCoordinates(lat, lon, apiKey, units)
            Log.d("WeatherRepository", "API Response: $response")
            handleResponse(response)
        } catch (e: Exception) {
            Result.Failure("Network error: ${e.message ?: "Unknown error"}")

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
