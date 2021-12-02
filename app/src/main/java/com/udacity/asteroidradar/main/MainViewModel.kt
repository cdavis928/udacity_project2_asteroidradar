package com.udacity.asteroidradar.main

import android.app.Application
import android.media.Image
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.MoshiNasaApi
import com.udacity.asteroidradar.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "MainViewModel"

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    //Create database singleton and repository
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _imageUrlLiveData = MutableLiveData<ImageOfDay>()
    val imageUrlLiveData: LiveData<ImageOfDay>
        get() = _imageUrlLiveData

    private var filterAsteroids = MutableLiveData(AsteroidFilters.ALL)
    val asteroidList = Transformations.switchMap(filterAsteroids) {
        when (it!!) {
            AsteroidFilters.WEEK -> asteroidRepository.thisWeeksAsteroids
            AsteroidFilters.TODAY -> asteroidRepository.todaysAsteroids
            else -> asteroidRepository.allAsteroids
        }
    }

    init {
        viewModelScope.launch {
            asteroidRepository.fetchAsteroids()
            fetchImageOfDay()
        }
    }

    // The ViewModelFactory for this view model
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }

    // Retrieve the image of the day
    private suspend fun fetchImageOfDay() {
        withContext(Dispatchers.IO) {
            try {

                //We have to use postValue now since we cant use value on a background thread
                _imageUrlLiveData.postValue(MoshiNasaApi.retrofitMoshiService.getImageOfDay())

            } catch (e: Exception) {
                Log.e(TAG, "Image error: ${e.message}")
            }
        }
    }

    //We'll use this to navigate from MainFragment to DetailFragment
    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail: LiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    //We'll use this to navigate from MainFragment to DetailFragment

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _navigateToAsteroidDetail.value = null
    }

    fun onToggleFilter(filter: AsteroidFilters) {
        filterAsteroids.postValue(filter)

    }
}

