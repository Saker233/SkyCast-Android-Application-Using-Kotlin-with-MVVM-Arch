package com.example.skycast.Repo

import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.CurrentResponseApi.*
import com.example.skycast.network.Result
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

val testCurrentResponseApi = CurrentResponseApi(
    base = "stations",
    clouds = Clouds(all = 20),
    cod = 200,
    coord = Coord(lat = 30.0, lon = 31.0),
    dt = 1609459200,
    id = 123456,
    main = Main(
        feelsLike = 25.0,
        grndLevel = 1000,
        humidity = 60,
        pressure = 1013,
        seaLevel = 1013,
        temp = 26.5,
        tempMax = 27.0,
        tempMin = 24.0
    ),
    name = "Cairo",
    rain = Rain(h = 0.0),
    sys = Sys(
        country = "EG",
        id = 1,
        sunrise = 1609477200,
        sunset = 1609515600,
        type = 1
    ),
    timezone = 7200,
    visibility = 10000,
    weather = listOf(
        Weather(
            description = "clear sky",
            icon = "01d",
            id = 800,
            main = "Clear"
        )
    ),
    wind = Wind(
        deg = 350,
        gust = 4.5,
        speed = 2.5
    )
)

class WeatherRepositoryTest {

    @Test
    fun getWeatherByCoordinates_success() = runBlockingTest {
        // Use the pre-initialized test data for a successful response
        val fakeApiService = FakeApiService(currentWeatherResponse = Response.success(testCurrentResponseApi))
        val fakePlaceDao = FakePlaceDao()
        val repository = WeatherRepository(fakeApiService, fakePlaceDao)

        // When calling getWeatherByCoordinates
        val result = repository.getWeatherByCoordinates(30.0, 30.0, "85f1176e73af023bdc219b8e180d44d6", "metric", "en")

        // Then the result should be Success
        assertTrue(result is Result.Success)
    }

    @Test
    fun getWeatherByCoordinates_failure() = runBlockingTest {
        val fakeApiService = FakeApiService(currentWeatherResponse = Response.error(404, okhttp3.ResponseBody.create(null, "")))
        val fakePlaceDao = FakePlaceDao()
        val repository = WeatherRepository(fakeApiService, fakePlaceDao)

        // When calling getWeatherByCoordinates
        val result = repository.getWeatherByCoordinates(30.0, 30.0, "85f1176e73af023bdc219b8e180d44d6", "metric", "en")

        // Then the result should be Failure
        assertTrue(result is Result.Failure)
    }
}
