package com.example.weatherapp.notification.factory

import com.example.weatherapp.model.Hour
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.util.DateUtils
import kotlin.properties.Delegates.notNull

abstract class BaseWeatherNotificationFactory : WeatherNotificationFactory {
    protected lateinit var todayHours: List<Hour>
    protected lateinit var todayRemainingHours: List<Hour>
    protected lateinit var todayWithTomorrowHours: MutableList<Hour>
    protected lateinit var tomorrowHours: List<Hour>

    protected lateinit var nowHour: Hour
    protected var nowHourAsInt by notNull<Int>()

    protected lateinit var localTime: String

    override fun createNotification(data: WeatherData): WeatherNotification? {

        localTime = data.location.localtime
        todayHours = data.daysForecast.first().hours

        nowHourAsInt = DateUtils.getHourFromDate(data.location.localtime)
        nowHour = todayHours[nowHourAsInt]

        todayRemainingHours = todayHours.subList(nowHourAsInt, todayHours.size)
        tomorrowHours = data.daysForecast[1].hours
        createTodayWithTomorrowHours()

        return create(data)
    }

    protected abstract fun create(data: WeatherData): WeatherNotification?

    protected fun isTodayHour(hour: Hour): Boolean {
        return hour.dateTime.substring(0, hour.dateTime.indexOf(" ")) ==
                localTime.substring(0, localTime.indexOf(" "))
    }

    protected fun precipitationAsString(hour: Hour): String =
        if (hour.isRain()) "Rain" else "Snow"

    private fun createTodayWithTomorrowHours() {
        todayWithTomorrowHours = mutableListOf()
        todayWithTomorrowHours.addAll(todayRemainingHours)

        if (tomorrowHours.size > nowHourAsInt) {
            todayWithTomorrowHours.addAll(tomorrowHours.subList(0, nowHourAsInt))
        }
    }
}