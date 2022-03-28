package com.example.weatherapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "current_weather",
    indices = [Index(value = ["location_id"])]
)
data class CurrentWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "location_id")
    var locationId: Int,

    @ColumnInfo(name = "temp_c")
    var tempC: Double,

    @ColumnInfo(name = "temp_f")
    var tempF: Double,

    @ColumnInfo(name = "feels_like_c")
    var feelsLikeTempC: Double,

    @ColumnInfo(name = "feels_like_f")
    var feelsLikeTempF: Double,

    @ColumnInfo(name = "condition_icon")
    var conditionIcon: String,

    @ColumnInfo(name = "condition_text")
    var conditionText: String,

    @ColumnInfo(name = "wind_kph")
    var windKph: Double,

    @ColumnInfo(name = "uv_index")
    var uvIndex: Int,

    @ColumnInfo(name = "pressure_mb")
    var pressureMb: Double,

    @ColumnInfo(name = "sunrise")
    var sunrise: String,

    @ColumnInfo(name = "sunset")
    var sunset: String,

    @ColumnInfo(name = "co")
    var co: Double,

    @ColumnInfo(name = "no2")
    var no2: Double,

    @ColumnInfo(name = "o3")
    var o3: Double,

    @ColumnInfo(name = "so2")
    var so2: Double,

    @ColumnInfo(name = "us_epa_index")
    var usEpaIndex: Int
)

