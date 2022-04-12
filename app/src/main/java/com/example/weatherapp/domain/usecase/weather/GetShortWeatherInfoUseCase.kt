package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class GetShortWeatherInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseUseCase<Location, ShortWeatherInfo?>() {

    override suspend fun execute(data: Location): ShortWeatherInfo? {
        return weatherRepository.getShortWeatherInfo(data)
    }
}