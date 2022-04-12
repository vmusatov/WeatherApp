package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class GetLocationsByNameUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<String, List<Location>>() {

    override suspend fun execute(data: String): List<Location> {
        return locationsRepository.getLocationsByName(data)
    }
}