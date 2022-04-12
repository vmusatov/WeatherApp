package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import com.example.weatherapp.util.DateUtils
import java.util.*
import javax.inject.Inject

data class Data(
    val location: Location,
    val forceLoad: Boolean
)

class GetWeatherDataUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseUseCase<Data, WeatherData?>() {

    override suspend fun execute(data: Data): WeatherData? {
        val needForceLoad = data.forceLoad || needForceLoad(data.location)

        val weatherData =
            weatherRepository.getWeatherDataByLocation(needForceLoad, data.location)

        weatherData?.let { weatherRepository.saveWeatherData(data.location, weatherData) }
        return weatherData
    }

    private fun needForceLoad(location: Location): Boolean {
        if (location.lastUpdated == null) {
            return true
        }

        val datesDiffInMin = DateUtils.datesDiffInMin(location.lastUpdated!!, Date())
        return datesDiffInMin > MAX_MINUTES_WITHOUT_UPDATE
    }

    companion object {
        private const val MAX_MINUTES_WITHOUT_UPDATE = 60
    }
}