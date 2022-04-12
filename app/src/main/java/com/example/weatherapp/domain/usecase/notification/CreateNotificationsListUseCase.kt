package com.example.weatherapp.domain.usecase.notification

import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class CreateNotificationsListUseCase @Inject constructor(
    private val notificationUseCases: Set<@JvmSuppressWildcards WeatherNotificationUseCase>
) : BaseUseCase<WeatherData, List<WeatherNotification>>() {

    override suspend fun execute(data: WeatherData): List<WeatherNotification> {
        val notifications = mutableListOf<WeatherNotification>()

        notificationUseCases.forEach { createNotification ->
            createNotification(data)?.let { notifications.add(it) }
        }

        return notifications
    }
}