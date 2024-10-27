package com.example.skycast.Alert.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val duration: Long,
    val type: String,
    val isActive: Boolean
)


