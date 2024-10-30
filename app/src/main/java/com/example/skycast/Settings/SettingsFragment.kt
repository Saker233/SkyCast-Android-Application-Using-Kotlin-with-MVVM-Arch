package com.example.skycast.Settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.skycast.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingsManager: SettingsManager
    private val temperatureUnitKey = SettingsManager.KEY_TEMPERATURE_UNIT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        settingsManager = SettingsManager(requireContext())

        val radioGroupUnits = view.findViewById<RadioGroup>(R.id.radioGroupUnits)
        val celsiusButton = view.findViewById<RadioButton>(R.id.btnCelsius)
        val fahrenheitButton = view.findViewById<RadioButton>(R.id.btnFahrenheit)
        val kelvinButton = view.findViewById<RadioButton>(R.id.btnKelvin)

        when (settingsManager.getTemperatureUnit()) {
            SettingsManager.UNIT_CELSIUS -> celsiusButton.isChecked = true
            SettingsManager.UNIT_FAHRENHEIT -> fahrenheitButton.isChecked = true
            SettingsManager.UNIT_KELVIN -> kelvinButton.isChecked = true
        }

        radioGroupUnits.setOnCheckedChangeListener { _, checkedId ->
            val unit = when (checkedId) {
                R.id.btnCelsius -> SettingsManager.UNIT_CELSIUS
                R.id.btnFahrenheit -> SettingsManager.UNIT_FAHRENHEIT
                R.id.btnKelvin -> SettingsManager.UNIT_KELVIN
                else -> SettingsManager.UNIT_CELSIUS
            }
            settingsManager.setTemperatureUnit(unit)
            Log.d("SettingsFragment", "Temperature unit changed to: $unit")
        }


        val radioGroupNotifications = view.findViewById<RadioGroup>(R.id.radioGroupNotifications)
        val enableNotificationsButton = view.findViewById<RadioButton>(R.id.btnEnableNotifications)
        val disableNotificationsButton = view.findViewById<RadioButton>(R.id.btnDisableNotifications)

        if (settingsManager.isNotificationsEnabled()) {
            enableNotificationsButton.isChecked = true
        } else {
            disableNotificationsButton.isChecked = true
        }

        radioGroupNotifications.setOnCheckedChangeListener { _, checkedId ->
            val notificationsEnabled = checkedId == R.id.btnEnableNotifications
            settingsManager.setNotifications(notificationsEnabled)
            Log.d("SettingsFragment", "Notifications enabled: $notificationsEnabled")
        }
    }
}



