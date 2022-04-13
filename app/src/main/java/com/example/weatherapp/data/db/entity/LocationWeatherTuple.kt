package com.example.weatherapp.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weatherapp.domain.model.Day
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.util.DateUtils

class LocationWeatherTuple(
    @Embedded
    val location: LocationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val current: CurrentWeatherEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val hours: List<HourEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val days: List<DayEntity>
) {
    fun toWeatherData(): WeatherData {
        val location = location.toLocation()
        val current = current.toCurrentWeather()
        val days = parseDaysForecast()
        val hours = WeatherData.parseDaysToHoursForecast(location.localtime, days)

        val lastUpdated = location.lastUpdated?.let {
            DateUtils.UPDATED_AT_DATE_FORMAT.format(it)
        }

        return WeatherData(location, current, hours, days, lastUpdated)
    }


    private fun parseDaysForecast(): List<Day> {
        return days.map { dayEntity ->
            val day = dayEntity.toDay()
            day.hours = hours.filter { it.dayId == dayEntity.id }.map { it.toHour() }
            day
        }
    }
}