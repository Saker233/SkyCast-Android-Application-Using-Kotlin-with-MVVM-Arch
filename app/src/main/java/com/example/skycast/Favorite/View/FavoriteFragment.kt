package com.example.skycast.Favorite.View

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.Favorite.ViewModel.FavoriteViewModel
import com.example.skycast.Favorite.ViewModel.FavoriteViewModelFactory
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.db.FavoritePlacesDatabase
import com.example.skycast.model.FavoritePlaceItem
import com.example.skycast.network.RetrofitHelper
import kotlinx.coroutines.launch
import com.example.skycast.network.Result


class FavoriteFragment : Fragment() {
    private lateinit var favoritePlaceAdapter: FavoritePlaceAdapter
    private val favoritePlaces = mutableListOf<FavoritePlaceItem>()

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var imgBtnAdd: ImageView

    private val REQUEST_CODE_MAP = 100

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.favRecycelr)
        imgBtnAdd = view.findViewById(R.id.imgBtnAdd)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoritePlaceAdapter = FavoritePlaceAdapter(favoritePlaces) { place ->
            favoriteViewModel.deletePlace(place)
        }
        recyclerView.adapter = favoritePlaceAdapter

        val apiService = RetrofitHelper.service
        val placeDao = FavoritePlacesDatabase.getDatabase(requireContext()).placeDao()
        val weatherRepository = WeatherRepository(apiService, placeDao)
        val factory = FavoriteViewModelFactory(weatherRepository)
        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        observeFavoritePlaces()

        imgBtnAdd.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAP)
        }

        return view
    }

    private fun observeFavoritePlaces() {
        lifecycleScope.launch {
            favoriteViewModel.getAllPlaces().collect { result ->
                Log.d("FavoriteFragment", "Result: $result")
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        val places = result.data
                        favoritePlaces.clear()
                        favoritePlaces.addAll(places)
                        favoritePlaceAdapter.notifyDataSetChanged()

                    }
                    is Result.Failure -> {
                        Log.e("FavoriteFragment", "Error loading places: ${result}")

                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAP && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("selected_latitude", 0.0)
            val longitude = data?.getDoubleExtra("selected_longitude", 0.0)
            val placeName = data?.getStringExtra("place_name") ?: "Unknown Location"

            if (latitude != null && longitude != null) {
                val newFavoritePlace = FavoritePlaceItem(latitude = latitude, longitude = longitude, placeName = placeName)
                favoriteViewModel.insertPlace(newFavoritePlace)
                Log.d("FavoriteFragment", "Inserted Place: $newFavoritePlace")

            }
        }
    }
}
