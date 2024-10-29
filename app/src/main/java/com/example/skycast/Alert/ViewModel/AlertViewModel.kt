package com.example.skycast.Alert.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.Alert.Model.Alert
import com.example.skycast.Alert.Model.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlertViewModel(private val repository: AlertRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts = _alerts.asStateFlow()

    init {
        fetchAllAlerts()
    }

    private fun fetchAllAlerts() {
        viewModelScope.launch {
            repository.getAllAlerts().collect { alertsList ->
                _alerts.value = alertsList
            }
        }
    }

    fun insertAlert(alert: Alert) {
        viewModelScope.launch {
            repository.insertAlert(alert)
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }
}