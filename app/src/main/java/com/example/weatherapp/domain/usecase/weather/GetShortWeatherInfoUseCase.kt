package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import com.example.weatherapp.domain.utils.WorkResult
import javax.inject.Inject

class GetShortWeatherInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseUseCase<Location, WorkResult<ShortWeatherInfo>>() {

    override suspend fun execute(data: Location): WorkResult<ShortWeatherInfo> {
        return weatherRepository.getShortWeatherInfo(data)
    }
}