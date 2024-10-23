package com.example.skycast.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.skycast.model.FavoritePlaceItem

@Database(entities = [FavoritePlaceItem::class], version = 1, exportSchema = false)
abstract class FavoritePlacesDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: FavoritePlacesDatabase? = null

        fun getDatabase(context: Context): FavoritePlacesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoritePlacesDatabase::class.java,
                    "favorite_places_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
