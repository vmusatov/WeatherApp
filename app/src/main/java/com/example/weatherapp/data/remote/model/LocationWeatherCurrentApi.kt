package com.example.weatherapp.data.remote.model

import com.example.weatherapp.domain.model.ShortWeatherData

data class LocationWeatherCurrentApi(
    val location: LocationApi,
    val current: WeatherCurrentApi
) {
    fun toShortWeatherData(): ShortWeatherData {
        return ShortWeatherData(
            locationName = location.name,
            tempC = current.tempC,
            tempF = current.tempF,
            conditionIconUrl = current.condition.icon
        )
    }
}