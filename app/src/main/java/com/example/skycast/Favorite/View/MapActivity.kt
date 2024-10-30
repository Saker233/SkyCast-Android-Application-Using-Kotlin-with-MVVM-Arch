//package com.example.skycast.Favorite.View
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Geocoder
//import android.os.Bundle
//import android.preference.PreferenceManager
//import android.util.Log
//import android.view.GestureDetector
//import android.view.MotionEvent
//import android.view.ScaleGestureDetector
//import android.widget.EditText
//import android.widget.ImageButton
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.lifecycle.ViewModelProvider
//import com.example.skycast.Favorite.ViewModel.FavoriteViewModel
//import com.example.skycast.Favorite.ViewModel.FavoriteViewModelFactory
//import com.example.skycast.R
//import com.example.skycast.Repo.WeatherRepository
//import com.example.skycast.db.FavoritePlacesDatabase
//import com.example.skycast.model.FavoritePlaceItem
//import com.example.skycast.network.RetrofitHelper
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import org.osmdroid.config.Configuration
//import org.osmdroid.library.BuildConfig
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.Marker
//import java.util.Locale
//
//class MapActivity : AppCompatActivity() {
//
//    private lateinit var mapView: MapView
//    private lateinit var latitude: String
//    private lateinit var longitude: String
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var imgAddLocation: ImageButton
//    private lateinit var marker: Marker
//    private lateinit var searchBar: EditText
//
//    private lateinit var favoriteViewModel: FavoriteViewModel
//    private lateinit var gestureDetector: GestureDetector
//
//    private lateinit var scaleGestureDetector: ScaleGestureDetector
//
//
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_map)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
//        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
//        Configuration.getInstance().osmdroidBasePath = cacheDir
//        Configuration.getInstance().osmdroidTileCache = cacheDir
//
//
//        mapView = findViewById(R.id.mapView)
//        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
//        mapView.setMultiTouchControls(true)
//        mapView.setBuiltInZoomControls(true)
//
//
//        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
//
//        imgAddLocation = findViewById(R.id.imgAddLocation)
//        searchBar = findViewById(R.id.searchBar)
//
//        searchBar.setOnEditorActionListener { _, _, _ ->
//            val locationName = searchBar.text.toString()
//            if (locationName.isNotEmpty()) {
//                searchLocation(locationName)
//            }
//            true
//        }
//
//        marker = Marker(mapView)
//        mapView.overlays.add(marker)
//
//
//        val repository = WeatherRepository(RetrofitHelper.service, FavoritePlacesDatabase.getDatabase(this).placeDao())
//        val factory = FavoriteViewModelFactory(repository)
//        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
//
//
//        imgAddLocation.setOnClickListener {
//
//            val selectedLatitude = marker.position.latitude
//            val selectedLongitude = marker.position.longitude
//
//
//            val geocoder = Geocoder(this, Locale.getDefault())
//            val addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1)
//
//            val placeName = if (addresses?.isNotEmpty() == true) {
//                addresses.get(0)?.getAddressLine(0)
//            } else {
//                "Unknown Location"
//            }
//
//            val fetchedPlaceName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
//
//            val newPlace = FavoritePlaceItem(
//                latitude = selectedLatitude,
//                longitude = selectedLongitude,
//                placeName = fetchedPlaceName
//            )
//
//            val resultIntent = Intent().apply {
//                putExtra("selected_latitude", selectedLatitude)
//                putExtra("selected_longitude", selectedLongitude)
//                putExtra("place_name", placeName)
//            }
//
//            setResult(RESULT_OK, resultIntent)
//
//            finish()
//
//        }
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            getCurrentLocation()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//
//        gestureDetector = GestureDetector(this, GestureListener())
//
//
//
//        mapView.setOnTouchListener { _, event ->
//            gestureDetector.onTouchEvent(event)
//            true
//        }
//    }
//
//
//
//    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
//        private var scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(this@MapActivity, ScaleListener())
//        private val scrollSpeedFactor = 0
//        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
//            val proj = mapView.projection
//            val geoPoint = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
//            moveMarkerTo(geoPoint)
//            return true
//        }
//
//        override fun onScroll(
//            e1: MotionEvent?,
//            e2: MotionEvent,
//            distanceX: Float,
//            distanceY: Float
//        ): Boolean {
//            if (e1 == null) return false
//
//            val proj = mapView.projection
//            val startGeoPoint = proj.fromPixels(e1.x.toInt(), e1.y.toInt()) as GeoPoint
//            val endGeoPoint = proj.fromPixels(e2.x.toInt(), e2.y.toInt()) as GeoPoint
//
//            // Calculate the change in position
//            val latAdjustment = (startGeoPoint.latitude - endGeoPoint.latitude) * scrollSpeedFactor
//            val lonAdjustment = (endGeoPoint.longitude - startGeoPoint.longitude) * scrollSpeedFactor
//
//            // Set the new center of the map
//            mapView.controller.setCenter(
//                GeoPoint(
//                    startGeoPoint.latitude + latAdjustment,
//                    startGeoPoint.longitude + lonAdjustment
//                )
//            )
//
//            return true
//        }
//    }
//
//
//    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            val scaleFactor = detector.scaleFactor
//
//            // Adjust zoom level based on the scale factor
//            if (scaleFactor > 1) {
//                // Zoom in
//                mapView.controller.zoomIn()
//            } else {
//                // Zoom out
//                mapView.controller.zoomOut()
//            }
//
//            return true
//        }
//    }
//
//
//
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            getCurrentLocation()
//        }
//    }
//
//    private fun getCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//        }
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val latitude = location.latitude
//                val longitude = location.longitude
//
//                Log.d("MapActivity", "Current Latitude: $latitude, Longitude: $longitude")
//
//                val geoPoint = GeoPoint(latitude, longitude)
//                mapView.controller.setZoom(18.0)
//                mapView.controller.setCenter(geoPoint)
//
//                val marker = Marker(mapView)
//                marker.position = geoPoint
//                marker.title = "Current Location"
//                mapView.overlays.add(marker)
//
//                mapView.invalidate()
//            } else {
//                Log.e("MapActivity", "Failed to retrieve location")
//            }
//        }
//    }
//    private fun moveMarkerTo(geoPoint: GeoPoint) {
//        marker.position = geoPoint
//        marker.title = "Selected Location"
//        mapView.controller.setCenter(geoPoint)
//        mapView.invalidate()
//    }
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
//
//    private fun searchLocation(locationName: String) {
//        val geocoder = Geocoder(this, Locale.getDefault())
//        Thread {
//            try {
//                val addresses = geocoder.getFromLocationName(locationName, 1)
//                if (addresses != null) {
//                    if (addresses.isNotEmpty()) {
//                        val location = addresses[0]
//                        val geoPoint = GeoPoint(location.latitude, location.longitude)
//
//                        runOnUiThread {
//                            moveMarkerTo(geoPoint)
//                            mapView.controller.setCenter(geoPoint)
//                            mapView.invalidate()
//                        }
//                    } else {
//                        runOnUiThread {
//                            Log.e("MapActivity", "Location not found")
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("MapActivity", "Geocoding error: ${e.message}")
//            }
//        }.start()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.overlays.clear()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//
//
//}


package com.example.skycast.Favorite.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageButton
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imgAddLocation: ImageButton
    private lateinit var searchBar: EditText
    private lateinit var favoriteViewModel: FavoriteViewModel
    private var lastMarker: Marker? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        Configuration.getInstance().osmdroidBasePath = cacheDir
        Configuration.getInstance().osmdroidTileCache = cacheDir

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(true)

        imgAddLocation = findViewById(R.id.imgAddLocation)
        searchBar = findViewById(R.id.searchBar)

        val repository = WeatherRepository(RetrofitHelper.service, FavoritePlacesDatabase.getDatabase(this).placeDao())
        val factory = FavoriteViewModelFactory(repository)
        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        searchBar.setOnEditorActionListener { _, _, _ ->
            val locationName = searchBar.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            }
            true
        }

        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val proj = mapView.projection
                val geoPoint = proj.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint

                lastMarker?.let { mapView.overlays.remove(it) }

                addMarker(geoPoint)
            }
            true
        }

        imgAddLocation.setOnClickListener {
            lastMarker?.let { marker ->
                val selectedLatitude = marker.position.latitude
                val selectedLongitude = marker.position.longitude

                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1)
                val placeName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"

                val newPlace = FavoritePlaceItem(latitude = selectedLatitude, longitude = selectedLongitude, placeName = placeName)
                val resultIntent = Intent().apply {
                    putExtra("selected_latitude", selectedLatitude)
                    putExtra("selected_longitude", selectedLongitude)
                    putExtra("place_name", placeName)
                }

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun addMarker(geoPoint: GeoPoint) {
        // Create a new marker
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.title = "Selected Location"

        lastMarker = marker

        mapView.overlays.add(marker)
        mapView.controller.setCenter(geoPoint)
        mapView.invalidate()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(geoPoint)

                addMarker(geoPoint)
            } else {
                Log.e("MapActivity", "Failed to retrieve location")
            }
        }
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
                            addMarker(geoPoint)
                            mapView.controller.setCenter(geoPoint)
                        }
                    } else {
                        Log.e("MapActivity", "Location not found")
                    }
                }
            } catch (e: Exception) {
                Log.e("MapActivity", "Geocoding error: ${e.message}")
            }
        }.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.overlays.clear()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}



