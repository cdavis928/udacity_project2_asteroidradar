package com.udacity.asteroidradar.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "AsteroidRepository"

class AsteroidRepository(private val database: AsteroidsDatabase) {

    private val startDate = LocalDateTime.now()
    private val endDate = LocalDateTime.now().plusDays(7)

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids(
            startDate.format(DateTimeFormatter.ISO_DATE),
            endDate.format(DateTimeFormatter.ISO_DATE)
        )) {
            it.asDomainModel()
        }

    val todaysAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroidsToday(
            startDate.format(
                DateTimeFormatter.ISO_DATE
            )
        )
    ) {
        it.asDomainModel()
    }

    val thisWeeksAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroidsThisWeek(
            startDate.format(DateTimeFormatter.ISO_DATE),
            endDate.format(DateTimeFormatter.ISO_DATE)
        )
    ) {
        it.asDomainModel()
    }

    suspend fun fetchAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                //Get the list of asteroids from our Nasa API
                var listResult = NasaApi.retrofitService.getAsteroids()
                // Create a JSONObject from the String we just received
                val jsonAsteroids = JSONObject(listResult)
                //Create a list with the JSONObject as the type
                var asteroidList = mutableListOf<JSONObject>()
                //Fill the list with the latest results
                asteroidList.add(jsonAsteroids)
                //Use the Udacity-provided parsing function on the list
                val convertedAsteroids = parseAsteroidsJsonResult(jsonAsteroids)

                database.asteroidDao.insertAllAsteroids(*convertedAsteroids.asDatabaseModel())
                Log.d(TAG, "Success")

            } catch (e: Exception) {
                Log.e(TAG, "Failed: ${e.message}")
            }
        }
    }
}