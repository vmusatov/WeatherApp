package com.example.weatherapp.notification.factory

import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.WeatherNotification

interface WeatherNotificationFactory {
    fun createNotification(data: WeatherData) : WeatherNotification?
}