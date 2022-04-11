package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

data class Data(
    val location: Location,
    val forceLoad: Boolean
)

class GetWeatherDataUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseUseCase<Data, WeatherData>() {

    override suspend fun execute(data: Data): WeatherData {
        return weatherRepository.getWeatherDataByLocation(data.forceLoad, data.location)
    }
}