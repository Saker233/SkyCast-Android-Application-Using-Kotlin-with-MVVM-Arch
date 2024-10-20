//package com.example.skycast.db
//
//import android.provider.DocumentsContract
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//
//@Dao
//interface WeatherDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertWeatherData(weatherData: DocumentsContract.Root)
//
//    @Query("SELECT * FROM weather WHERE name = :cityName")
//    suspend fun getWeatherByCity(cityName: String): DocumentsContract.Root?
//
//    @Query("SELECT * FROM weather WHERE lat = :lat AND lon = :lon")
//    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): DocumentsContract.Root?
//}