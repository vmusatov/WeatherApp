package com.example.weatherapp.notification

import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.factory.WeatherNotificationFactory

class WeatherNotificationsBuilder {

    private val factories = mutableListOf<WeatherNotificationFactory>()

    fun addFactory(factory: WeatherNotificationFactory) {
        this.factories.add(factory)
    }

    fun buildNotificationsList(data: WeatherData): List<WeatherNotification> {
        val notifications = mutableListOf<WeatherNotification>()

        factories.forEach { factory ->
            factory.createNotification(data)?.let { notifications.add(it) }
        }

        return notifications
    }

}