package com.example.weatherapp.notification.factory

import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification

interface WeatherNotificationFactory {
    fun createNotification(data: WeatherData) : WeatherNotification?
}