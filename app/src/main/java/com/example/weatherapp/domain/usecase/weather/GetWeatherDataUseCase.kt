package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import com.example.weatherapp.domain.utils.WorkResult
import com.example.weatherapp.util.DateUtils
import java.util.*
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository,
    private val weatherRepository: WeatherRepository
) : BaseUseCase<GetWeatherDataUseCase.Data, WorkResult<WeatherData>>() {

    override suspend fun execute(data: Data): WorkResult<WeatherData> {
        val location = locationsRepository.getLocationByUrl(data.location.url) ?: data.location

        val needForceLoad = data.forceLoad || needForceLoad(location)
        return weatherRepository.getWeatherDataByLocation(needForceLoad, location)
    }

    private fun needForceLoad(location: Location): Boolean {
        if (location.lastUpdated == null) {
            return true
        }

        val datesDiffInMin = DateUtils.datesDiffInMin(location.lastUpdated!!, Date())
        return datesDiffInMin > MAX_MINUTES_WITHOUT_UPDATE
    }

    data class Data(val location: Location, val forceLoad: Boolean)

    companion object {
        private const val MAX_MINUTES_WITHOUT_UPDATE = 60
    }
}