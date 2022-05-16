package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.utils.WorkResult
import kotlinx.coroutines.flow.Flow

interface LocationsRepository {

    suspend fun saveLocation(location: Location): Long

    suspend fun deleteLocation(location: Location)

    suspend fun getSelectedLocation(): Location?

    suspend fun getAllLocations(): List<Location>

    suspend fun autocompleteLocationsByName(name: String): WorkResult<List<Location>>

    suspend fun getLocationByUrl(url: String): Location?

    suspend fun updateLocationPosition(location: Location, position: Int)

    suspend fun updateLocationLocalTime(locationUrl: String, localtime: String)

    suspend fun setLocationIsSelected(location: Location)

    suspend fun setLocationIsNotSelected(location: Location)

    suspend fun getLocationsCount(): Int

    fun listenSelectedLocation(): Flow<Location?>

    fun listenAddLocation(): Flow<Location>
}