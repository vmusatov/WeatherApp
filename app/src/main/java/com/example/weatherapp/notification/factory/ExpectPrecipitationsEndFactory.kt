package com.example.weatherapp.notification.factory

import com.example.weatherapp.model.Hour
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.util.DateUtils
import javax.inject.Inject

class ExpectPrecipitationsEndFactory @Inject constructor() : BaseWeatherNotificationFactory() {

    override fun create(data: WeatherData): WeatherNotification? {

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

        val endHourAsInt = DateUtils.getHourFromDate(endHour.dateTime)
        return if (isTodayHour(endHour)) {
            "${precipitationAsString(nowHour)} ends at ${endHourAsInt}:00"
        } else {
            "${precipitationAsString(nowHour)} ends tomorrow at ${endHourAsInt}:00"
        }
    }
}