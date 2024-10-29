package com.example.skycast.Alert.Model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert)

    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<Alert>>

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("SELECT * FROM alerts WHERE id = :alertId LIMIT 1")
    suspend fun getAlertById(alertId: Long): Alert?

    @Query("DELETE FROM alerts WHERE alertId = :alertId")
    suspend fun deleteAlertByAlertId(alertId: Long)
}
