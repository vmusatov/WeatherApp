package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class GetAllLocationsUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<Unit, List<Location>> {

    override suspend fun execute(data: Unit): List<Location> {
        return locationsRepository.getAllLocations()
    }
}