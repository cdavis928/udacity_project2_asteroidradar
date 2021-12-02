package com.udacity.asteroidradar.database

import androidx.room.Entity
import com.squareup.moshi.Json
import java.io.Serializable

data class ImageOfDay(
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    @Json(name = "url") val imgUrl: String)