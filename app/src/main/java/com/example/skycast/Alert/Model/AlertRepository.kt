package com.example.skycast.Alert.Model

import kotlinx.coroutines.flow.Flow

class AlertRepository(private val alertDao: AlertDao) {

    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()

    suspend fun insertAlert(alert: Alert) = alertDao.insertAlert(alert)

    suspend fun deleteAlert(alert: Alert) = alertDao.deleteAlert(alert)
}
