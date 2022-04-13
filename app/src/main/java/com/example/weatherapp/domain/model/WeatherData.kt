package com.example.weatherapp.domain.model

import com.example.weatherapp.util.DateUtils

data class WeatherData(
    val location: Location,
    val current: CurrentWeather,
    val hoursForecast: List<Hour>,
    val daysForecast: List<Day>,
    val lastUpdated: String?
) {
    companion object {

        fun parseDaysToHoursForecast(localTime: String, forecastDays: List<Day>): List<Hour> {
            val hours = mutableListOf<Hour>()

            if (forecastDays.isNotEmpty()) {
                val nowHourAsInt = DateUtils.getHourFromDate(localTime)
                val todayHours = forecastDays.first().hours

                if (todayHours.size > nowHourAsInt) {
                    hours.addAll(todayHours.subList(nowHourAsInt, todayHours.size))
                }

                if (forecastDays.size > 1 && forecastDays[1].hours.size > nowHourAsInt) {
                    val tomorrowHours = forecastDays[1].hours
                    hours.addAll(tomorrowHours.subList(0, nowHourAsInt))
                }
            }

            return hours
        }
    }
}





