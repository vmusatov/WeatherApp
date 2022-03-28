package com.example.weatherapp.model

import com.example.weatherapp.data.remote.model.LocationWeatherCurrentApi

data class LocationWeatherInfo(
    val locationName: String,
    val tempC: Double,
    val tempF: Double,
    val conditionIconUrl: String
) {
    companion object {
        fun from(from: LocationWeatherCurrentApi): LocationWeatherInfo {
            return LocationWeatherInfo(
                locationName = from.location.name,
                tempC = from.current.tempC,
                tempF = from.current.tempF,
                conditionIconUrl = from.current.condition.icon
            )
        }
    }
}
