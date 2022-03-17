package com.example.weatherapp.notification.factory

import com.example.weatherapp.data.remote.model.WeatherForecast
import com.example.weatherapp.notification.WeatherNotification

interface WeatherNotificationFactory {
    fun createNotification(weatherForecast: WeatherForecast) : WeatherNotification?
}