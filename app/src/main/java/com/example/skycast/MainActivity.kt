package com.example.skycast

import android.annotation.SuppressLint
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.skycast.Favorite.View.FavoriteFragment
import com.example.skycast.Home.View.HomeFragment



class MainActivity : AppCompatActivity() {



    lateinit var viewPager: ViewPager2



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
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> FavoriteFragment()
                else -> HomeFragment()
            }
        }
    }
}