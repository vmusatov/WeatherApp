package com.example.weatherapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.util.DateUtils

@Entity(
    tableName = "locations"
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "localtime")
    var localtime: String,

    @ColumnInfo(name = "region")
    var region: String,

    @ColumnInfo(name = "country")
    var country: String,

    @ColumnInfo(name = "lat")
    var lat: Double,

    @ColumnInfo(name = "lon")
    var lon: Double,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "is_selected")
    var isSelected: Int,

    @ColumnInfo(name = "position")
    var position: Int,

    @ColumnInfo(name = "last_updated")
    var lastUpdated: String?
) {
    fun toLocation(): Location {
        val lastUpdated = lastUpdated?.let { DateUtils.dateTimeFromString(it) }
        return Location(
            name = name,
            localtime = localtime,
            region = region,
            country = country,
            lat = lat,
            lon = lon,
            url = url,
            isSelected = isSelected == 1,
            position = position,
            lastUpdated = lastUpdated
        )
    }

    companion object {
        fun from(from: Location): LocationEntity {
            val lastUpdated = from.lastUpdated?.let { DateUtils.dateTimeToString(it) }
            return LocationEntity(
                id = 0,
                name = from.name,
                localtime = from.localtime,
                region = from.region,
                country = from.country,
                lat = from.lat,
                lon = from.lon,
                url = from.url,
                isSelected = if (from.isSelected) 1 else 0,
                position = from.position,
                lastUpdated = lastUpdated
            )
        }
    }
}