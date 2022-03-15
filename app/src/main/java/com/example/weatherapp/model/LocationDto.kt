package com.example.weatherapp.model

import com.example.weatherapp.data.remote.model.SearchLocation

data class LocationDto (
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String,
    val isSelected: Boolean = false,
    val position: Int = -1,
) {
    companion object {
        fun fromSearchLocation(from: SearchLocation): LocationDto {
            return LocationDto(
                name = from.name,
                region = from.region,
                country = from.country,
                lat = from.lat,
                lon = from.lon,
                url = from.url,
            )
        }
    }
}