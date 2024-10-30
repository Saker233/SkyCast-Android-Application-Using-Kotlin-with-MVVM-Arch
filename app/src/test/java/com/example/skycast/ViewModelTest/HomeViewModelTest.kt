package com.example.skycast.ViewModelTest

import android.content.SharedPreferences
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.Home.ViewModel.HomeViewModel
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.Settings.SettingsManager
import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.FiveDaysResponseApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.skycast.network.Result
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.runners.JUnit4


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var settingsManager: SettingsManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()



    @Before
    fun setUp() {
        sharedPreferences = mockk(relaxed = true)
        sharedPreferencesEditor = mockk(relaxed = true)
        every { sharedPreferences.edit() } returns sharedPreferencesEditor

        // Mocking SettingsManager to return the mocked SharedPreferences
        settingsManager = mockk(relaxed = true)
        every { settingsManager.preferences } returns sharedPreferences

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        // Mocking Repository
        repository = mockk(relaxed = true)

        // Initializing the ViewModel with mocks
        homeViewModel = HomeViewModel(repository, settingsManager)
    }


    @After
    fun tearDown() {
        // Cleanup if necessary
    }



    @Test
    fun fetchWeatherByCoordinates_weatherDataIsNotNull() = runTest {
        // Arrange
        val mockResponse = CurrentResponseApi(
            base = "stations",
            clouds = CurrentResponseApi.Clouds(all = 75),
            cod = 200,
            coord = CurrentResponseApi.Coord(lat = 37.7749, lon = -122.4194),
            dt = 1633036800,
            id = 5391959,
            main = CurrentResponseApi.Main(
                feelsLike = 293.15,
                grndLevel = 1013,
                humidity = 60,
                pressure = 1015,
                seaLevel = 1013,
                temp = 294.15,
                tempMax = 295.15,
                tempMin = 293.15
            ),
            name = "San Francisco",
            rain = CurrentResponseApi.Rain(h = 0.5),
            sys = CurrentResponseApi.Sys(
                country = "US",
                id = 5122,
                sunrise = 1633009216,
                sunset = 1633052156,
                type = 1
            ),
            timezone = -25200,
            visibility = 10000,
            weather = listOf(
                CurrentResponseApi.Weather(
                    description = "clear sky",
                    icon = "01d",
                    id = 800,
                    main = "Clear"
                )
            ),
            wind = CurrentResponseApi.Wind(deg = 200, gust = 5.0, speed = 3.0)
        )

        coEvery { repository.getWeatherByCoordinates(30.0, 31.0, "85f1176e73af023bdc219b8e180d44d6", "metric", "en") } returns Result.Success(mockResponse)

        // Act
        homeViewModel.fetchWeatherByCoordinates(30.0, 31.0, "85f1176e73af023bdc219b8e180d44d6")

        assertThat(homeViewModel.weatherData.first(), not(nullValue()))
    }


    @Test
    fun fetchFiveDayWeatherByCoordinates_forecastDataIsNotNull() = runTest {
        // Arrange
        val mockForecastResponse = FiveDaysResponseApi(
            city = FiveDaysResponseApi.City(
                coord = FiveDaysResponseApi.City.Coord(lat = 30.0, lon = 31.0),
                country = "EG",
                id = 1,
                name = "Sample City",
                population = 1000000,
                sunrise = 1600000000,
                sunset = 1600040000,
                timezone = 7200
            ),
            cnt = 5,
            cod = "200",
            message = 0,
            list = listOf(
                FiveDaysResponseApi.data(
                    dt = 1600010000,
                    dtTxt = "2024-10-30 12:00:00",
                    main = FiveDaysResponseApi.data.Main(
                        temp = 295.0,
                        feelsLike = 293.0,
                        tempMin = 293.0,
                        tempMax = 297.0,
                        pressure = 1013,
                        seaLevel = 1013,
                        grndLevel = 1011,
                        humidity = 60,
                        tempKf = 0.0
                    ),
                    weather = listOf(
                        FiveDaysResponseApi.data.Weather(
                            id = 800,
                            main = "Clear",
                            description = "clear sky",
                            icon = "01d"
                        )
                    ),
                    clouds = FiveDaysResponseApi.data.Clouds(all = 0),
                    wind = FiveDaysResponseApi.data.Wind(speed = 4.1, deg = 80, gust = 5.5),
                    visibility = 10000,
                    pop = 0.0,
                    rain = FiveDaysResponseApi.data.Rain(h = 0.0),
                    sys = FiveDaysResponseApi.data.Sys(pod = "d")
                )
            )
        )

        coEvery { repository.getFiveDayWeatherByCoordinates(30.0, 31.0, "API_KEY", "metric", "en") } returns Result.Success(mockForecastResponse)

        // Act
        homeViewModel.fetchFiveDayWeatherByCoordinates(30.0, 31.0, "API_KEY")

        // Assert
        assertThat(homeViewModel.forecastData.first(), not(nullValue()))
    }



}
