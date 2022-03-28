package com.example.weatherapp.notification.factory

import com.example.weatherapp.model.Hour
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.util.DateUtils

class ExpectPrecipitationsFactory : BaseWeatherNotificationFactory() {

    override fun create(data: WeatherData): WeatherNotification? {

        if (nowHour.isHavePrecipitation()) {
            return null
        }

        todayWithTomorrowHours.firstOrNull { it.isHavePrecipitation() }?.let {
            return WeatherNotification(
                "Precipitations",
                precipitationsStartAt(it)
            )
        }
        return null
    }

    private fun precipitationsStartAt(startHour: Hour): String {
        val startHourAsInt = DateUtils.getHourFromDate(startHour.dateTime)

        return if (isTodayHour(startHour)) {
            "${precipitationAsString(startHour)} expected at $startHourAsInt:00"
        } else {
            "${precipitationAsString(startHour)} expected tomorrow at $startHourAsInt:00"
        }
    }
}