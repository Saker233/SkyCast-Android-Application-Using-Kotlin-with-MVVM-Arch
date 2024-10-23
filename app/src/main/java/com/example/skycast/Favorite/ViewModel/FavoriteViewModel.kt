package com.example.skycast.Favorite.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.Repo.WeatherRepository
import com.example.skycast.model.FavoritePlaceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.skycast.network.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {


    private val _favoritePlaces = MutableStateFlow<List<FavoritePlaceItem>>(emptyList())
    val favoritePlaces: StateFlow<List<FavoritePlaceItem>> get() = _favoritePlaces


    init {
        getAllPlaces()
    }

    fun insertPlace(place: FavoritePlaceItem) {
        viewModelScope.launch {
            repository.insertPlace(place)
            getAllPlaces()
        }
    }

    fun deletePlace(place: FavoritePlaceItem) {
        viewModelScope.launch {
            repository.deletePlace(place)
            getAllPlaces()
        }
    }

    fun getAllPlaces(): Flow<Result<List<FavoritePlaceItem>>> = flow {

        repository.getAllPlaces().collect { places ->
            emit(Result.Success(places))
        }

    }



}