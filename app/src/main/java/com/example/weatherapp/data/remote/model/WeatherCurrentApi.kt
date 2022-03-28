package com.example.weatherapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherCurrentApi(
    @SerializedName("last_updated")
    var lastUpdated: String,

    @SerializedName("temp_c")
    var tempC: Double,

    @SerializedName("temp_f")
    var tempF: Double,

    @SerializedName("feelslike_c")
    var feelsLikeTempC: Double,

    @SerializedName("feelslike_f")
    var feelsLikeTempF: Double,

    @SerializedName("condition")
    var condition: ConditionApi,

    @SerializedName("wind_kph")
    var windKph: Double,

    @SerializedName("humidity")
    var humidity: Int,

    @SerializedName("uv")
    var uvIndex: Int,

    @SerializedName("pressure_mb")
    var pressureMb: Double,

    @SerializedName("air_quality")
    var airQuality: AirQualityApi
)

data class AirQualityApi(
    val co: Double,
    val no2: Double,
    val o3: Double,
    val so2: Double,
    @SerializedName("us-epa-index")
    val usEpaIndex: Int
)