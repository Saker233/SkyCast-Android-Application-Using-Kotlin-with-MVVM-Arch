package com.example.skycast.Home.View

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skycast.Home.ViewModel.HomeViewModel
import com.example.skycast.Home.ViewModel.HomeViewModelFactory
import com.example.skycast.R
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.model.CurrentResponseApi
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView
import java.util.Calendar
import com.example.skycast.network.Result
import com.example.skycast.network.RetrofitHelper
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var weatherView: WeatherView
    private lateinit var cityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var currentTempTextView: TextView
    private lateinit var maxTempTextView: TextView
    private lateinit var minTempTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var bgImage: ImageView

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        weatherView = view.findViewById(R.id.weatherView)
        cityTextView = view.findViewById(R.id.txtCity)
        windTextView = view.findViewById(R.id.windTxt)
        statusTextView = view.findViewById(R.id.txtStatus)
        currentTempTextView = view.findViewById(R.id.currentTempTxt)
        maxTempTextView = view.findViewById(R.id.maxTempTxt)
        minTempTextView = view.findViewById(R.id.minTempTxt)
        humidityTextView = view.findViewById(R.id.humidityTxt)
        bgImage = view.findViewById(R.id.bgImage)


        val apiService = RetrofitHelper.service
        val weatherRepository = WeatherRepository(apiService)
        val factory = HomeViewModelFactory(weatherRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)


        viewModel.fetchWeatherByCity("Tokyo", "85f1176e73af023bdc219b8e180d44d6")

        observeWeatherData()

        return view
    }

    private fun observeWeatherData() {
        lifecycleScope.launch {
            viewModel.weatherData.collect { result ->
                when (result) {
                    is Result.Success -> {
                        updateWeatherUI(result.data)
                    }
                    is Result.Failure -> {
                    }
                    is Result.Loading -> {

                    }
                }
            }
        }
    }

    fun updateWeatherUI(weatherResponse: CurrentResponseApi) {
        weatherResponse?.let {
            // Update UI components with weather data
            cityTextView.text = it.name ?: "Unknown City"
            currentTempTextView.text = "${Math.round((it.main?.temp ?: 0.0) - 273.15)}°C"
            maxTempTextView.text = "${Math.round((it.main?.tempMax ?: 0.0) - 273.15)}°C"
            minTempTextView.text = "${Math.round((it.main?.tempMin ?: 0.0) - 273.15)}°C"
            humidityTextView.text = "${Math.round((it.main?.humidity ?: 0.0).toDouble())}%"
            windTextView.text = "${Math.round(it.wind?.speed ?: 0.0)} m/s"
            statusTextView.text = it.weather?.get(0)?.main ?: "No Status"

            val apiTimeInMillis = (it.dt?.toLong() ?: 0L) * 1000L
            val timezoneOffsetInMillis = (it.timezone?.toLong() ?: 0L) * 1000L
            val isNight = isNightNow(apiTimeInMillis, timezoneOffsetInMillis)
            val drawable = if(isNight) R.drawable.night_bg
            else {
                setDynamicWallpaper(it.weather?.get(0)?.icon?:"-")
            }

            bgImage.setImageResource(drawable)
            setEffectRain(it.weather?.get(0)?.icon?:"-")
        }
    }





    fun isNightNow(apiTimeInMillis: Long, timezoneOffsetInMillis: Long): Boolean {
        val localTimeInMillis = apiTimeInMillis + timezoneOffsetInMillis
        val calendar = Calendar.getInstance().apply {
            timeInMillis = localTimeInMillis
        }
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        Log.d("HomeFragment", "Local time hour: $currentHour")
        return currentHour < 6 || currentHour >= 18
    }



    private fun setDynamicWallpaper(icon : String) : Int {
        return when(icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.sunny_bg
            }
            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else -> 0
        }
    }



    private fun setEffectRain(icon: String) {
         when(icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
            }
            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)
            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)
            }
        }
    }

    private fun initWeatherView(type: PrecipType) {
        weatherView.apply {
            setWeatherData(type)
            angle = 20
            emissionRate = 100.0f
        }
    }

}