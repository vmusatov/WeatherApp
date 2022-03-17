package com.example.weatherapp.notification.factory

import com.example.weatherapp.data.remote.model.Hour
import com.example.weatherapp.data.remote.model.WeatherForecast
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.util.DateUtils

class ExpectPrecipitationsEndFactory : BaseWeatherNotificationFactory() {

    override fun create(forecast: WeatherForecast): WeatherNotification? {

        if (nowHour.isNotHavePrecipitation()) {
            return null
        }

        val endHour = todayWithTomorrowHours.firstOrNull { it.isNotHavePrecipitation() }

        return WeatherNotification(
            "Precipitations",
            precipitationsEndAt(endHour)
        )
    }

    private fun precipitationsEndAt(endHour: Hour?): String {
        if (endHour == null) {
            return "The ${precipitationAsString(nowHour).lowercase()} won't end anytime soon"
        }

        val endHourAsInt = DateUtils.getHourFromDate(endHour.time)
        return if (isTodayHour(nowHour)) {
            "${precipitationAsString(nowHour)} ends tomorrow at ${endHourAsInt}:00"
        } else {
            "${precipitationAsString(nowHour)} ends at ${endHourAsInt}:00"
        }
    }
}