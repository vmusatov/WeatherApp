package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherInfo
import com.example.weatherapp.domain.model.WeatherData

interface WeatherRepository {

    suspend fun getWeatherDataByLocation(forceLoad: Boolean, location: Location): WeatherData?

    suspend fun getShortWeatherInfo(location: Location): ShortWeatherInfo?

    suspend fun clearWeatherData(location: Location)
}