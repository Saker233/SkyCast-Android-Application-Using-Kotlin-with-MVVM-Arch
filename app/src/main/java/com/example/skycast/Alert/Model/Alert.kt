package com.example.skycast.Alert.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val alertId: Long,
    val title: String,
    val duration: Long,
    val time: String,
    val type: String = "Weather Alert",
    val isActive: Boolean = true,
    val latitude: Double,
    val longitude: Double
)

