package com.example.weatherapp.data.remote.model

import com.example.weatherapp.domain.model.ShortWeatherInfo

data class LocationWeatherCurrentApi(
    val location: LocationApi,
    val current: WeatherCurrentApi
) {
    fun toShortWeatherInfo(): ShortWeatherInfo {
        return ShortWeatherInfo(
            locationName = location.name,
            tempC = current.tempC,
            tempF = current.tempF,
            conditionIconUrl = current.condition.icon
        )
    }
}