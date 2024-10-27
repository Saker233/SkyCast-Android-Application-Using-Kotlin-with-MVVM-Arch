package com.example.skycast.Alert.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.Alert.Model.Alert
import com.example.skycast.Alert.Model.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import com.example.skycast.network.Result

class AlertViewModel(private val repository: AlertRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> get() = _alerts

    init {
        getAllAlerts()
    }

    fun insertAlert(alert: Alert) {
        viewModelScope.launch {
            repository.insertAlert(alert)
            getAllAlerts()
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            getAllAlerts()
        }
    }

    fun getAllAlerts(): Flow<Result<List<Alert>>> = flow {
        repository.getAllAlerts().collect { alerts ->
            emit(Result.Success(alerts))
        }
    }
}
