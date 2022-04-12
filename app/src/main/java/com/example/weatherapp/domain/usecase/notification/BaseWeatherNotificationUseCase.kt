package com.example.weatherapp.domain.usecase.notification

import com.example.weatherapp.domain.model.Hour
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import com.example.weatherapp.domain.usecase.BaseUseCase
import com.example.weatherapp.util.DateUtils
import kotlin.properties.Delegates

typealias WeatherNotificationUseCase = BaseUseCase<WeatherData, WeatherNotification?>

abstract class BaseWeatherNotificationUseCase : WeatherNotificationUseCase() {
    protected lateinit var todayHours: List<Hour>
    protected lateinit var todayRemainingHours: List<Hour>
    protected lateinit var todayWithTomorrowHours: MutableList<Hour>
    protected lateinit var tomorrowHours: List<Hour>

    protected lateinit var nowHour: Hour
    protected var nowHourAsInt by Delegates.notNull<Int>()

    protected lateinit var localTime: String

    override suspend fun execute(data: WeatherData): WeatherNotification? {

        localTime = data.location.localtime
        todayHours = data.daysForecast.first().hours

        nowHourAsInt = DateUtils.getHourFromDate(data.location.localtime)
        nowHour = todayHours[nowHourAsInt]

        todayRemainingHours = todayHours.subList(nowHourAsInt, todayHours.size)
        tomorrowHours = data.daysForecast[1].hours
        createTodayWithTomorrowHours()

        return createNotification(data)
    }

    abstract fun createNotification(data: WeatherData): WeatherNotification?

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