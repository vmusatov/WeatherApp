package com.example.weatherapp.domain.usecase.location

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenSelectedLocationUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : BaseUseCase<Unit, Flow<Location?>> {

    override suspend fun execute(data: Unit): Flow<Location?> {
        return locationsRepository.listenSelectedLocation()
    }
}