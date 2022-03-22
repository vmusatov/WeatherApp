package com.example.weatherapp.model

import com.example.weatherapp.data.remote.model.SearchLocation
import java.util.*

data class LocationDto (
    var name: String,
    var region: String,
    var country: String,
    var lat: Double,
    var lon: Double,
    var url: String,
    var isSelected: Boolean = false,
    var position: Int = -1,
    var lastUpdated: Date? = null
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