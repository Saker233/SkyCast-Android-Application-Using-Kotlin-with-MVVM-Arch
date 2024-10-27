package com.example.skycast.Alert.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.Alert.Model.Alert
import com.example.skycast.Alert.Model.AlertDatabase
import com.example.skycast.Alert.Model.AlertRepository
import com.example.skycast.R
import com.example.skycast.Alert.ViewModel.AlertViewModel
import com.example.skycast.Alert.ViewModel.AlertViewModelFactory
import com.example.skycast.network.Result
import kotlinx.coroutines.launch

class AlertFragment : Fragment() {

    private lateinit var alertAdapter: AlertAdapter
    private val alerts = mutableListOf<Alert>()

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var imgBtnAddAlert: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alert, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAlert)
        imgBtnAddAlert = view.findViewById(R.id.imgBtnAddAlert)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        alertAdapter = AlertAdapter(alerts, { alert -> alertViewModel.deleteAlert(alert) })
        recyclerView.adapter = alertAdapter

        val alertDao = AlertDatabase.getDatabase(requireContext()).alertDao()
        val alertRepository = AlertRepository(alertDao)
        val factory = AlertViewModelFactory(alertRepository)
        alertViewModel = ViewModelProvider(this, factory).get(AlertViewModel::class.java)

        observeAlerts()

        imgBtnAddAlert.setOnClickListener {


        }

        return view
    }

    private fun observeAlerts() {
        lifecycleScope.launch {
            alertViewModel.getAllAlerts().collect { result ->
                when (result) {
                    is Result.Success -> {
                        alerts.clear()
                        alerts.addAll(result.data)
                        alertAdapter.notifyDataSetChanged()
                    }
                    is Result.Failure -> {

                    }

                    Result.Loading -> {
                    }
                }
            }
        }
    }
}
