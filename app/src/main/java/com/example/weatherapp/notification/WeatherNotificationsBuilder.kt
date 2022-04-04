package com.example.weatherapp.notification

import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.factory.WeatherNotificationFactory
import javax.inject.Inject

class WeatherNotificationsBuilder @Inject constructor(
    private val factories: Set<@JvmSuppressWildcards WeatherNotificationFactory>
) {

    fun buildNotificationsList(data: WeatherData): List<WeatherNotification> {
        val notifications = mutableListOf<WeatherNotification>()

        factories.forEach { factory ->
            factory.createNotification(data)?.let { notifications.add(it) }
        }

        return notifications
    }
}