package com.example.skycast.Favorite.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.EditText
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
    private lateinit var searchBar: EditText

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var gestureDetector: GestureDetector


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
        searchBar = findViewById(R.id.searchBar)

        searchBar.setOnEditorActionListener { _, _, _ ->
            val locationName = searchBar.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            }
            true
        }

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

        gestureDetector = GestureDetector(this, GestureListener())


//        mapView.setOnTouchListener { _, event ->
//            if (event.action == android.view.MotionEvent.ACTION_UP) {
//                val proj = mapView.projection
//                val geoPoint = proj.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
//
//                moveMarkerTo(geoPoint)
//            }
//            false
//        }

        mapView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val proj = mapView.projection
            val geoPoint = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
            moveMarkerTo(geoPoint)
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val startPoint = GeoPoint(mapView.projection.fromPixels(e1!!.x.toInt(), e1.y.toInt()).latitude,
                mapView.projection.fromPixels(e1.x.toInt(), e1.y.toInt()).longitude)
            val endPoint = GeoPoint(mapView.projection.fromPixels(e2!!.x.toInt(), e2.y.toInt()).latitude,
                mapView.projection.fromPixels(e2.x.toInt(), e2.y.toInt()).longitude)

            val newGeoPoint = GeoPoint(
                startPoint.latitude - (distanceY / mapView.height * (mapView.boundingBox.latNorth - mapView.boundingBox.latSouth)),
                startPoint.longitude + (distanceX / mapView.width * (mapView.boundingBox.lonEast - mapView.boundingBox.lonWest))
            )

            mapView.controller.setCenter(newGeoPoint)

            return true
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

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        Thread {
            try {
                val addresses = geocoder.getFromLocationName(locationName, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val location = addresses[0]
                        val geoPoint = GeoPoint(location.latitude, location.longitude)

                        runOnUiThread {
                            moveMarkerTo(geoPoint)
                            mapView.controller.setCenter(geoPoint)
                            mapView.invalidate()
                        }
                    } else {
                        runOnUiThread {
                            Log.e("MapActivity", "Location not found")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MapActivity", "Geocoding error: ${e.message}")
            }
        }.start()
    }

}