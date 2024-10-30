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

        // Set the radio button based on the saved preference
        when (settingsManager.getTemperatureUnit()) {
            SettingsManager.UNIT_CELSIUS -> celsiusButton.isChecked = true
            SettingsManager.UNIT_FAHRENHEIT -> fahrenheitButton.isChecked = true
            SettingsManager.UNIT_KELVIN -> kelvinButton.isChecked = true
        }

        // Save the selected unit preference when changed
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
    }
}

