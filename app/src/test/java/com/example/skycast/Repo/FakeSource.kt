package com.example.skycast.Repo


import com.example.skycast.db.PlaceDao
import com.example.skycast.model.CurrentResponseApi
import com.example.skycast.model.FavoritePlaceItem
import com.example.skycast.model.FiveDaysResponseApi
import com.example.skycast.network.ApiService
import com.example.skycast.network.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeApiService(
    private val currentWeatherResponse: Response<CurrentResponseApi>? = null,
    private val fiveDayForecastResponse: Response<FiveDaysResponseApi>? = null
) : ApiService {

    override suspend fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): Response<CurrentResponseApi> {
        return currentWeatherResponse ?: throw Exception("No response available")
    }

    override suspend fun getFiveDayWeatherByCoordinates(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): Response<FiveDaysResponseApi> {
        return fiveDayForecastResponse ?: throw Exception("No response available")
    }
}

class FakePlaceDao(private val places: MutableList<FavoritePlaceItem> = mutableListOf()) : PlaceDao {

    override suspend fun insert(place: FavoritePlaceItem) {
        places.add(place)
    }

    override suspend fun deletePlace(placeId: Long) {
        places.removeIf { it.id == placeId }
    }


    override fun getAllPlaces(): Flow<List<FavoritePlaceItem>> {
        return flow { emit(places) }
    }
}
