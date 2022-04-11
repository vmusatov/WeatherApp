package com.example.weatherapp.notification.factory

import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import javax.inject.Inject

class NoPrecipitationsFactory @Inject constructor() : BaseWeatherNotificationFactory() {

    override fun create(data: WeatherData): WeatherNotification? {

        val hourWithRain = todayRemainingHours.firstOrNull { it.isRain() }
        val hourWithSnow = todayRemainingHours.firstOrNull { it.isSnow() }

        if (nowHourAsInt < 22 && hourWithRain == null && hourWithSnow == null) {
            return WeatherNotification(
                "Precipitation",
                "No precipitation expected today"
            )
        }

        return null
    }
}