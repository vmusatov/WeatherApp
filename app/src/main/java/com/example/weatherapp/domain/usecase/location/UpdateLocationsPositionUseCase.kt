package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class UpdateLocationsPositionUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<List<Location>, Unit>() {

    override suspend fun execute(data: List<Location>) {
        data.forEachIndexed { index, location ->
            locationsRepository.updateLocationPosition(location, index)
        }
    }
}