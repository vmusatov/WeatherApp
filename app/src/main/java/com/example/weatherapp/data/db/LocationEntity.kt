package com.example.weatherapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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

)