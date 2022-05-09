package com.example.weatherapp.domain.usecase.notification

import com.example.weatherapp.di.DefaultDispatcher
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TempTomorrowUseCase @Inject constructor(
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : BaseWeatherNotificationUseCase(dispatcher) {

    override suspend fun createNotification(data: WeatherData): WeatherNotification? {
        if (nowHourAsInt < 16) {
            return null
        }

        val todayAverage = todayHours.map { it.tempC }.average()
        val tomorrowAverage = tomorrowHours.map { it.tempC }.average()

        val diff = (tomorrowAverage - todayAverage).toInt()

        val temperatureTomorrow = WeatherNotification(title = "Temperature tomorrow")
        var undefined = false
        when (diff) {
            in -100..-11 -> temperatureTomorrow.message = "Much colder than today"
            in -10..-6 -> temperatureTomorrow.message = "Colder than today"
            in -5..-3 -> temperatureTomorrow.message = "A little colder than today"

            in -2..2 -> temperatureTomorrow.message = "Almost the same as today"

            in 3..5 -> temperatureTomorrow.message = "A little warmer than today"
            in 6..10 -> temperatureTomorrow.message = "Warmer than today"
            in 11..100 -> temperatureTomorrow.message = "Much warmer than today"

            else -> undefined = true
        }

        if (!undefined) {
            return temperatureTomorrow
        }
        return null
    }
}