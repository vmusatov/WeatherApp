package com.example.weatherapp.domain.usecase.notification

import com.example.weatherapp.domain.model.Hour
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import com.example.weatherapp.util.DateUtils
import javax.inject.Inject

class ExpectPrecipitationsUseCase @Inject constructor() : BaseWeatherNotificationUseCase() {

    override fun createNotification(data: WeatherData): WeatherNotification? {

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