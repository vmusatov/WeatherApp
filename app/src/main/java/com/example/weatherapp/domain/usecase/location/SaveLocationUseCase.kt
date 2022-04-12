package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<Location, Long>() {

    override suspend fun execute(data: Location): Long {
        data.position = locationsRepository.getLocationsCount()

        if (data.position == 0) {
            data.isSelected = true
        }

        return locationsRepository.saveLocation(data)
    }
}