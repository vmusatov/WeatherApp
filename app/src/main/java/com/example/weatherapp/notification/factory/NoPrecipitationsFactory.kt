package com.example.weatherapp.notification.factory

import com.example.weatherapp.data.remote.model.LocationWeatherForecast
import com.example.weatherapp.notification.WeatherNotification

class NoPrecipitationsFactory : BaseWeatherNotificationFactory() {

    override fun create(forecast: LocationWeatherForecast): WeatherNotification? {

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