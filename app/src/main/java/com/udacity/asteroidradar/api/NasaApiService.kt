package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.ImageOfDay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.sql.Date

enum class NasaApiFilter(val value: String) {
    SHOW_URL("url")
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

private val retrofitMoshi = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

interface NasaApiService {

    @GET("/neo/rest/v1/feed")
    //Specifying a String as Call's generic type ensure that Retrofit deserializes
    //the Retrofit object into a String

    //This lovely code borrowed from Ashwani K of the Udacity Knowledge forum
    suspend fun getAsteroids(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): String

    @GET("/planetary/apod")
    suspend fun getImageOfDay(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): ImageOfDay

}

object NasaApi {
    val retrofitService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
}

object MoshiNasaApi {
    val retrofitMoshiService: NasaApiService by lazy {
        retrofitMoshi.create(NasaApiService::class.java)
    }
}