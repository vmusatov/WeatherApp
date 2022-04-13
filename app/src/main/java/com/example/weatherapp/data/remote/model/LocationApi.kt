package com.example.weatherapp.data.remote.model

import com.example.weatherapp.domain.model.Location

data class LocationApi(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val localtime: String
) {
    fun toLocation(): Location {
        return Location(
            name = name,
            region = region,
            country = country,
            localtime = localtime,
            lat = lat,
            lon = lon,
            url = "",
        )
    }
}

data class SearchLocationApi(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
) {
    fun toLocation(): Location {
        return Location(
            name = name,
            region = region,
            country = country,
            localtime = "",
            lat = lat,
            lon = lon,
            url = url,
        )
    }
}
