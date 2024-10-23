package com.example.skycast.Favorite.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.Favorite.ViewModel.FavoriteViewModel
import com.example.skycast.Favorite.ViewModel.FavoriteViewModelFactory
import com.example.skycast.R
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.db.FavoritePlacesDatabase
import com.example.skycast.model.FavoritePlaceItem
import com.example.skycast.network.RetrofitHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imgAddLocation: ImageButton
    private lateinit var marker: Marker

    private lateinit var favoriteViewModel: FavoriteViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        imgAddLocation = findViewById(R.id.imgAddLocation)

        marker = Marker(mapView)
        mapView.overlays.add(marker)


        val repository = WeatherRepository(RetrofitHelper.service, FavoritePlacesDatabase.getDatabase(this).placeDao())
        val factory = FavoriteViewModelFactory(repository)
        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)


        imgAddLocation.setOnClickListener {

            val selectedLatitude = marker.position.latitude
            val selectedLongitude = marker.position.longitude


            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1)

            val placeName = if (addresses?.isNotEmpty() == true) {
                addresses.get(0)?.getAddressLine(0)
            } else {
                "Unknown Location"
            }

            val fetchedPlaceName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"

            val newPlace = FavoritePlaceItem(
                latitude = selectedLatitude,
                longitude = selectedLongitude,
                placeName = fetchedPlaceName
            )








            val resultIntent = Intent().apply {
                putExtra("selected_latitude", selectedLatitude)
                putExtra("selected_longitude", selectedLongitude)
                putExtra("place_name", placeName)
            }

            setResult(RESULT_OK, resultIntent)

            finish()

        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }


        mapView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val proj = mapView.projection
                val geoPoint = proj.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint

                moveMarkerTo(geoPoint)
            }
            true
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                Log.d("MapActivity", "Current Latitude: $latitude, Longitude: $longitude")

                // Set the location on the map
                val geoPoint = GeoPoint(latitude, longitude)
                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(geoPoint)

                // Add a marker at the user's current location
                val marker = Marker(mapView)
                marker.position = geoPoint
                marker.title = "Current Location"
                mapView.overlays.add(marker)

                mapView.invalidate()
            } else {
                Log.e("MapActivity", "Failed to retrieve location")
            }
        }
    }
    private fun moveMarkerTo(geoPoint: GeoPoint) {
        marker.position = geoPoint
        marker.title = "Selected Location"
        mapView.controller.setCenter(geoPoint)
        mapView.invalidate()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}