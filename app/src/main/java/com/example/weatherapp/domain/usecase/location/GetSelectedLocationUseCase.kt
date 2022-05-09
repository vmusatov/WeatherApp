package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class GetSelectedLocationUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<Unit, Location?> {

    override suspend fun execute(data: Unit): Location? {
        return locationsRepository.getSelectedLocation()
    }
}