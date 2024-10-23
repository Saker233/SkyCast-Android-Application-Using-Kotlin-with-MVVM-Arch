package com.example.skycast.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.model.FavoritePlaceItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: FavoritePlaceItem)

    @Query("SELECT * FROM favorite_places")
    fun getAllPlaces(): Flow<List<FavoritePlaceItem>>

    @Query("DELETE FROM favorite_places WHERE id = :placeId")
    suspend fun deletePlace(placeId: Long)
}
