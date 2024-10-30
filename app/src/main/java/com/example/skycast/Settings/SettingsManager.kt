package com.example.skycast.Settings

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    val preferences: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        const val KEY_LANGUAGE = "language"
        const val KEY_NOTIFICATIONS = "notifications"

        const val UNIT_CELSIUS = "Celsius"
        const val UNIT_FAHRENHEIT = "Fahrenheit"
        const val UNIT_KELVIN = "Kelvin"

        const val LANGUAGE_ENGLISH = "English"
        const val LANGUAGE_ARABIC = "Arabic"

        const val NOTIFICATIONS_ENABLED = "Enabled"
        const val NOTIFICATIONS_DISABLED = "Disabled"
    }

    fun setTemperatureUnit(unit: String) {
        preferences.edit().putString(KEY_TEMPERATURE_UNIT, unit).apply()
    }

    fun getTemperatureUnit(): String {
        return preferences.getString(KEY_TEMPERATURE_UNIT, UNIT_CELSIUS) ?: UNIT_CELSIUS
    }

    fun setLanguage(language: String) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String {
        return preferences.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
    }

    fun setNotifications(enabled: Boolean) {
        preferences.edit().putString(KEY_NOTIFICATIONS, if (enabled) NOTIFICATIONS_ENABLED else NOTIFICATIONS_DISABLED).apply()
    }

    fun isNotificationsEnabled(): Boolean {
        return preferences.getString(KEY_NOTIFICATIONS, NOTIFICATIONS_ENABLED) == NOTIFICATIONS_ENABLED
    }
}
