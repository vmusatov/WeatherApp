package com.example.weatherapp.model

import com.example.weatherapp.data.db.entity.LocationEntity
import com.example.weatherapp.data.remote.model.LocationApi
import com.example.weatherapp.data.remote.model.SearchLocationApi
import com.example.weatherapp.util.DateUtils
import java.util.*

data class Location(
    var name: String,
    var region: String,
    var country: String,
    var localtime: String,
    var lat: Double,
    var lon: Double,
    var url: String,
    var isSelected: Boolean = false,
    var position: Int = -1,
    var lastUpdated: Date? = null
) {
    fun toEntity(): LocationEntity {
        val lastUpdated = lastUpdated?.let { DateUtils.dateTimeToString(it) }
        return LocationEntity(
            id = 0,
            name = name,
            localtime = localtime,
            region = region,
            country = country,
            lat = lat,
            lon = lon,
            url = url,
            isSelected = if (isSelected) 1 else 0,
            position = position,
            lastUpdated = lastUpdated
        )
    }

    companion object {
        fun from(from: LocationEntity): Location {
            val lastUpdated = from.lastUpdated?.let { DateUtils.dateTimeFromString(it) }
            return Location(
                name = from.name,
                localtime = from.localtime,
                region = from.region,
                country = from.country,
                lat = from.lat,
                lon = from.lon,
                url = from.url,
                isSelected = from.isSelected == 1,
                position = from.position,
                lastUpdated = lastUpdated
            )
        }

        fun from(from: SearchLocationApi): Location {
            return Location(
                name = from.name,
                region = from.region,
                country = from.country,
                localtime = "",
                lat = from.lat,
                lon = from.lon,
                url = from.url,
            )
        }

        fun from(from: LocationApi): Location {
            return Location(
                name = from.name,
                region = from.region,
                country = from.country,
                localtime = from.localtime,
                lat = from.lat,
                lon = from.lon,
                url = "",
            )
        }
    }
}