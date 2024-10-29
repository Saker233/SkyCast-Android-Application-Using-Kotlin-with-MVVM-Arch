package com.example.skycast.Alert.Model

import kotlinx.coroutines.flow.Flow

class AlertRepository(private val alertDao: AlertDao) {

    suspend fun insertAlert(alert: Alert) {
        alertDao.insertAlert(alert)
    }

    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()

    suspend fun deleteAlert(alert: Alert) {
        alertDao.deleteAlert(alert)
    }

    suspend fun deleteAlertByAlertId(alertId: Long) = alertDao.deleteAlertByAlertId(alertId)

}
