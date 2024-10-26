package com.example.skycast.Favorite.View


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.model.FavoritePlaceItem



class FavoritePlaceAdapter(
    private val places: List<FavoritePlaceItem>,
    private val onDeleteClick: (FavoritePlaceItem) -> Unit,
    private val onPlaceClick: (Double, Double, String) -> Unit
) : RecyclerView.Adapter<FavoritePlaceAdapter.FavoritePlaceViewHolder>() {

    inner class FavoritePlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.tv_place_name)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnRemove)

        fun bind(favoritePlace: FavoritePlaceItem) {
            placeName.text = favoritePlace.placeName
            deleteButton.setOnClickListener {
                onDeleteClick(favoritePlace)
            }
            itemView.setOnClickListener {
                onPlaceClick(favoritePlace.latitude, favoritePlace.longitude, favoritePlace.placeName)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoritePlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritePlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size
}
