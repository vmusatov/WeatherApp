package com.example.weatherapp.data.utils

import com.example.weatherapp.data.db.entity.DayEntity
import com.example.weatherapp.data.db.entity.HourEntity
import com.example.weatherapp.domain.model.Day
import com.example.weatherapp.domain.model.Hour
import com.example.weatherapp.util.DateUtils

fun parseDaysForecast(days: List<DayEntity>, hours: List<HourEntity>): List<Day> {
    return days.map { dayEntity ->
        val day = dayEntity.toDay()
        day.hours = hours.filter { it.dayId == dayEntity.id }.map { it.toHour() }
        day
    }
}

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