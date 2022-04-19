package com.example.weatherapp.domain.model

data class WeatherData(
    val location: Location,
    val current: CurrentWeather,
    val hoursForecast: List<Hour>,
    val daysForecast: List<Day>,
    val lastUpdated: String?
)




