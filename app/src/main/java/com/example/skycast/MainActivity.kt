package com.example.skycast

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.skycast.Home.View.HomeFragment
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.network.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.skycast.network.Result
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


class MainActivity : AppCompatActivity() {

//    private lateinit var weatherRepository: WeatherRepository
//    private val apiKey = "85f1176e73af023bdc219b8e180d44d6"

    private lateinit var viewPager: ViewPager2


//    private val objectMapper = jacksonObjectMapper()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        val pagerAdapter = MyPagerAdapter(this)
        viewPager.adapter = pagerAdapter

    }

    private inner class MyPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 1

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                else -> HomeFragment()
            }
        }
    }
}