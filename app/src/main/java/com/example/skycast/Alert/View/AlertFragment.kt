// AlertFragment.kt

package com.example.skycast.Alert.View

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.Alert.Model.Alert
import com.example.skycast.Alert.Model.AlertDatabase
import com.example.skycast.Alert.Model.AlertRepository
import com.example.skycast.Alert.ViewModel.AlertViewModel
import com.example.skycast.Alert.ViewModel.AlertViewModelFactory
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.Settings.SettingsManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*

class AlertFragment : Fragment() {

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertAdapter: AlertAdapter
    private val alerts = mutableListOf<Alert>()
    private lateinit var settingsManager: SettingsManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_alert, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAlert)
        val imgBtnAddAlert = view.findViewById<ImageView>(R.id.imgBtnAddAlert)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        alertAdapter = AlertAdapter(alerts) { alert -> deleteAlert(alert) }
        recyclerView.adapter = alertAdapter

        val alertDao = AlertDatabase.getDatabase(requireContext()).alertDao()
        val repository = AlertRepository(alertDao)
        val factory = AlertViewModelFactory(repository)
        alertViewModel = ViewModelProvider(this, factory).get(AlertViewModel::class.java)
        settingsManager = SettingsManager(requireContext())

        imgBtnAddAlert.setOnClickListener {
            Log.d("AlertFragment", "Add Alert button clicked")

            val notificationsEnabled = settingsManager.isNotificationsEnabled()
            Log.d("AlertFragment", "Notifications enabled status: $notificationsEnabled")

            if (notificationsEnabled) {
                Log.d("AlertFragment", "Notifications are enabled - showing Add Alert dialog")
                showAddAlertDialog()
            } else {
                Log.d("AlertFragment", "Notifications are disabled - showing Snackbar with error")
                showNotificationsDisabledError(view)
            }
        }


        observeAlerts()
        return view
    }

    private fun isNotificationsEnabled(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("notifications", "Enabled") == "Enabled"
    }

    private fun showNotificationsDisabledError(view: View) {
        Snackbar.make(view, "Notifications are disabled. Enable them in Settings to add alerts.", Snackbar.LENGTH_LONG)
            .setAction("Settings") {
                navigateToSettingsFragment()
            }
            .show()
    }

    private fun navigateToSettingsFragment() {
        (requireActivity() as MainActivity).viewPager.setCurrentItem(3, true)
    }


    private fun showAddAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_alert, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val durationEditText = dialogView.findViewById<EditText>(R.id.durationEditText)
        val timeEditText = dialogView.findViewById<EditText>(R.id.timeEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Alert")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleEditText.text.toString().trim()
                val timeInput = timeEditText.text.toString().trim()
                val durationStr = durationEditText.text.toString().trim()
                val duration = durationStr.toLongOrNull()?.times(1000) ?: 0L

                if (title.isNotBlank() && timeInput.isNotEmpty() && duration > 0) {
                    val timeParts = timeInput.split(":")
                    if (timeParts.size == 2) {
                        val hour = timeParts[0].toIntOrNull()
                        val minute = timeParts[1].toIntOrNull()

                        if (hour != null && minute != null && hour in 0..23 && minute in 0..59) {
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                            }

                            val alertId = System.currentTimeMillis()
                            val alert = Alert(
                                id = alertId,
                                title = title,
                                alertId = alertId,
                                duration = duration,
                                time = timeInput,
                                latitude = 30.0711,
                                longitude = 31.0211
                            )
                            alertViewModel.insertAlert(alert)

                            scheduleAlarm(alert, calendar)
                            Toast.makeText(requireContext(), "Alert scheduled", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Invalid time format. Use HH:mm.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun scheduleAlarm(alert: Alert, calendar: Calendar) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java).apply {
            putExtra("title", alert.title)
            putExtra("duration", alert.duration / 1000 / 60) // Convert to minutes
            putExtra("alertId", alert.id)
            putExtra("apiKey", "85f1176e73af023bdc219b8e180d44d6")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alert.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("AlertFragment", "Alarm scheduled for: ${calendar.time}")
    }

    private fun observeAlerts() {
        viewLifecycleOwner.lifecycleScope.launch {
            alertViewModel.alerts.collect { alerts ->
                this@AlertFragment.alerts.clear()
                this@AlertFragment.alerts.addAll(alerts)
                alertAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteAlert(alert: Alert) {
        alertViewModel.deleteAlert(alert)
        Toast.makeText(requireContext(), "Alert deleted", Toast.LENGTH_SHORT).show()
    }
}
