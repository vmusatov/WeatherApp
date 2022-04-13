package com.example.weatherapp.domain.model

data class ShortWeatherInfo(
    val locationName: String,
    val tempC: Double,
    val tempF: Double,
    val conditionIconUrl: String
)