package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.utils.WorkResult

interface WeatherRepository {

    suspend fun getWeatherDataByLocation(forceLoad: Boolean, location: Location): WorkResult<WeatherData>

    suspend fun getShortWeatherInfo(location: Location): WorkResult<ShortWeatherData>

    suspend fun clearWeatherData(location: Location)
}