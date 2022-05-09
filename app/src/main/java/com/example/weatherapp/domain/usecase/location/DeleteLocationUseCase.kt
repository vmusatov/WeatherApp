package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.di.DefaultDispatcher
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository,
    private val weatherRepository: WeatherRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : BaseUseCase<Location, Unit> {

    override suspend fun execute(data: Location) = withContext(dispatcher) {
        locationsRepository.getLocationByUrl(data.url)?.let { entity ->
            if (entity.isSelected) {
                val notSelectedLocation =
                    locationsRepository.getAllLocations()
                        .sortedBy { it.position }
                        .firstOrNull { !it.isSelected }

                notSelectedLocation?.let { locationsRepository.setLocationIsSelected(it) }
            }
        }

        weatherRepository.clearWeatherData(data)
        locationsRepository.deleteLocation(data)
    }
}