//package com.example.skycast.db
//
//import android.content.Context
//import android.provider.DocumentsContract
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//
//
//
//@Database(entities = [DocumentsContract.Root::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
//abstract class WeatherDatabase : RoomDatabase() {
//
//    abstract fun weatherDao(): WeatherDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: WeatherDatabase? = null
//
//        fun getDatabase(context: Context): WeatherDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    WeatherDatabase::class.java,
//                    "weather_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}