package com.example.weatherapp.notification

import com.example.weatherapp.data.remote.model.WeatherForecast
import com.example.weatherapp.notification.factory.WeatherNotificationFactory

class WeatherNotificationsBuilder {

    private val factories = mutableListOf<WeatherNotificationFactory>()

    fun addFactory(factory: WeatherNotificationFactory) {
        this.factories.add(factory)
    }

    fun buildNotificationsList(forecast: WeatherForecast): List<WeatherNotification> {
        val notifications = mutableListOf<WeatherNotification>()

        factories.forEach { factory ->
            factory.createNotification(forecast)?.let { notifications.add(it) }
        }

        return notifications
    }

}