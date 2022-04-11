package com.example.weatherapp.domain.model

import com.example.weatherapp.data.db.entity.LocationWithWeatherTuple
import com.example.weatherapp.data.remote.model.LocationWeatherForecastApi
import com.example.weatherapp.util.DateUtils
import java.util.Date

data class WeatherData(
    val location: Location,
    val current: CurrentWeather,
    val hoursForecast: List<Hour>,
    val daysForecast: List<Day>,
    val lastUpdated: String?
) {
    companion object {
        fun from(apiModel: LocationWeatherForecastApi): WeatherData {
            val location = Location.from(apiModel.location)
            val current = CurrentWeather.from(apiModel)
            val days = apiModel.forecast.forecastDays.map { Day.from(it) }
            val hours = parseHoursForecast(apiModel.location.localtime, days)
            val lastUpdated = DateUtils.UPDATED_AT_DATE_FORMAT.format(Date())

            return WeatherData(location, current, hours, days, lastUpdated)
        }

        fun from(entity: LocationWithWeatherTuple): WeatherData {
            val location = Location.from(entity.location)
            val current = CurrentWeather.from(entity.current)
            val days = parseDaysForecast(entity)
            val hours = parseHoursForecast(entity.location.localtime, days)

            val lastUpdated = location.lastUpdated?.let {
                DateUtils.UPDATED_AT_DATE_FORMAT.format(it)
            }

            return WeatherData(location, current, hours, days, lastUpdated)
        }

        private fun parseDaysForecast(from: LocationWithWeatherTuple): List<Day> {
            return from.days.map { dayEntity ->
                val day = Day.from(dayEntity)
                day.hours = from.hours
                    .filter { it.dayId == dayEntity.id }
                    .map { Hour.from(it) }
                day
            }
        }

        private fun parseHoursForecast(
            localTime: String,
            forecastDays: List<Day>
        ): List<Hour> {
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




