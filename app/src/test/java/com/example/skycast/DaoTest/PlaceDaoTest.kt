package com.example.skycast.DaoTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.db.FavoritePlacesDatabase
import com.example.skycast.db.PlaceDao
import com.example.skycast.model.FavoritePlaceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class PlaceDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: FavoritePlacesDatabase
    private lateinit var placeDao: PlaceDao

    @Before
    fun setup() {
        // Create an in-memory database using FavoritePlacesDatabase
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavoritePlacesDatabase::class.java
        ).allowMainThreadQueries().build()

        placeDao = database.placeDao()
    }

    @After
    fun tearDown() {

        database.close()
    }

    @Test
    fun insertPlace_retrievesPlace() = runBlockingTest {
        val place = FavoritePlaceItem(
            id = 1L,
            latitude = 30.0,
            longitude = 31.0,
            placeName = "Test Place"
        )

        placeDao.insert(place)

        val allPlaces = placeDao.getAllPlaces().first()
        assertThat(allPlaces.size, `is`(1))
        assertThat(allPlaces[0], `is`(place))
    }

    @Test
    fun deletePlace_placeIsDeleted() = runBlockingTest {
        val place = FavoritePlaceItem(
            id = 2L,
            latitude = 40.0,
            longitude = -74.0,
            placeName = "Delete Test Place"
        )
        placeDao.insert(place)

        var allPlaces = placeDao.getAllPlaces().first()
        assertThat(allPlaces.size, `is`(1))

        placeDao.deletePlace(place.id)

        allPlaces = placeDao.getAllPlaces().first()
        assertThat(allPlaces.size, `is`(0))
    }
}
