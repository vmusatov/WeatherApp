package com.example.weatherapp.data.remote.model

data class LocationWeatherCurrentApi(
    val location: LocationApi,
    val current: WeatherCurrentApi
)