package com.example.weatherapp.domain.model

data class ShortWeatherData(
    val locationName: String,
    val tempC: Double,
    val tempF: Double,
    val conditionIconUrl: String
)