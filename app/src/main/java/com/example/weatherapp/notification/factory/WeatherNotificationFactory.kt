package com.example.weatherapp.notification.factory

import com.example.weatherapp.data.remote.model.LocationWeatherForecast
import com.example.weatherapp.notification.WeatherNotification

interface WeatherNotificationFactory {
    fun createNotification(weatherForecast: LocationWeatherForecast) : WeatherNotification?
}