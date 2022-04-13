package com.example.weatherapp.domain.model

data class CurrentWeather(
    var tempC: Double,
    var tempF: Double,
    var feelsLikeTempC: Double,
    var feelsLikeTempF: Double,
    var conditionIcon: String,
    var conditionText: String,
    var windKph: Double,
    var uvIndex: Int,
    var astronomy: Astronomy,
    var pressureMb: Double,
    var co: Double,
    var no2: Double,
    var o3: Double,
    var so2: Double,
    var usEpaIndex: Int
)