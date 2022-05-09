package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import com.example.weatherapp.domain.utils.WorkResult
import javax.inject.Inject

class GetShortWeatherDataUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseUseCase<Location, WorkResult<ShortWeatherData>> {

    override suspend fun execute(data: Location): WorkResult<ShortWeatherData> {
        return weatherRepository.getShortWeatherInfo(data)
    }
}