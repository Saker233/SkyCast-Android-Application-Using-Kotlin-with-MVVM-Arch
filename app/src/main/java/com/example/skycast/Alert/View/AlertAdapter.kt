package com.example.skycast.Alert.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.Alert.Model.Alert
import com.example.skycast.R

class AlertAdapter(
    private val alerts: List<Alert>,
    private val onDeleteClick: (Alert) -> Unit
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alertTitle: TextView = itemView.findViewById(R.id.tv_alert_title)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnRemoveAlert)

        fun bind(alert: Alert) {
            alertTitle.text = alert.title
            deleteButton.setOnClickListener {
                onDeleteClick(alert)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alerts[position])
    }

    override fun getItemCount(): Int = alerts.size
}
