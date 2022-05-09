package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class SetLocationIsSelectedUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<Location, Unit> {

    override suspend fun execute(data: Location) {
        locationsRepository.getSelectedLocation()?.let { old ->
            locationsRepository.setLocationIsNotSelected(old)
        }

        locationsRepository.setLocationIsSelected(data)
    }
}