package com.example.weatherapp.domain.model

import com.example.weatherapp.data.remote.model.LocationWeatherCurrentApi

data class ShortWeatherInfo(
    val locationName: String,
    val tempC: Double,
    val tempF: Double,
    val conditionIconUrl: String
) {
    companion object {
        fun from(from: LocationWeatherCurrentApi): ShortWeatherInfo {
            return ShortWeatherInfo(
                locationName = from.location.name,
                tempC = from.current.tempC,
                tempF = from.current.tempF,
                conditionIconUrl = from.current.condition.icon
            )
        }
    }
}
