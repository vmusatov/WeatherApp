package com.example.weatherapp.domain.usecase.notification

import com.example.weatherapp.domain.model.Hour
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import com.example.weatherapp.util.DateUtils
import javax.inject.Inject

class ExpectPrecipitationsEndUseCase @Inject constructor() : BaseWeatherNotificationUseCase() {

    override fun createNotification(data: WeatherData): WeatherNotification? {

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