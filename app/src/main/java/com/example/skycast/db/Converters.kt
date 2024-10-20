//package com.example.skycast.db
//
//import androidx.room.TypeConverter
//import com.google.android.gms.awareness.state.Weather
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class Converters {
//
//    @TypeConverter
//    fun fromWeatherList(value: List<Weather>): String {
//        val gson = Gson()
//        val type = object : TypeToken<List<Weather>>() {}.type
//        return gson.toJson(value, type)
//    }
//
//    @TypeConverter
//    fun toWeatherList(value: String): List<Weather> {
//        val gson = Gson()
//        val type = object : TypeToken<List<Weather>>() {}.type
//        return gson.fromJson(value, type)
//    }
//}