package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.db.entity.LocationEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.utils.safeApiCall
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.LocationListener
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.utils.WorkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationsRepositoryImpl(
    private val locationsDao: LocationsDao,
    private val weatherApi: WeatherApi,
    private val externalScope: CoroutineScope
) : LocationsRepository {

    private val selectedLocationListeners = mutableSetOf<LocationListener>()
    private val addLocationListeners = mutableSetOf<LocationListener>()

    override suspend fun saveLocation(location: Location): Long {
        val locationId = externalScope.async {
            locationsDao.insert(LocationEntity.from(location))
        }.await()

        addLocationListeners.forEach { it(location) }

        return locationId
    }

    override suspend fun deleteLocation(location: Location) {
        externalScope.launch {
            locationsDao.deleteByUrl(location.url)
        }.join()

        if (getLocationsCount() == 0) {
            selectedLocationListeners.forEach { it(null) }
        }
    }

    override suspend fun getSelectedLocation(): Location? {
        return locationsDao.getSelectedLocation()?.toLocation()
    }

    override suspend fun getAllLocations(): List<Location> {
        return locationsDao.getAllLocations().map { it.toLocation() }
    }

    override suspend fun autocompleteLocationsByName(name: String): WorkResult<List<Location>> {
        return safeApiCall { weatherApi.getSearchResult(name) }.map { locations ->
            locations.map { it.toLocation() }
        }
    }

    override suspend fun getLocationByUrl(url: String): Location? {
        return locationsDao.getLocationByUrl(url)?.toLocation()
    }

    override suspend fun updateLocationPosition(location: Location, position: Int) {
        externalScope.launch { locationsDao.updatePosition(location.url, position) }.join()
    }

    override suspend fun updateLocationLocalTime(locationUrl: String, localtime: String) {
        externalScope.launch { locationsDao.updateLocaltime(locationUrl, localtime) }.join()
    }

    override suspend fun setLocationIsSelected(location: Location) {
        externalScope.launch {
            locationsDao.getLocationByUrl(location.url)?.let {
                it.isSelected = 1
                locationsDao.update(it)
                selectedLocationListeners.forEach { listener -> listener(location) }
            }
        }.join()
    }

    override suspend fun setLocationIsNotSelected(location: Location) {
        locationsDao.getLocationByUrl(location.url)?.let {
            it.isSelected = 0
            locationsDao.update(it)
        }
    }

    override suspend fun getLocationsCount(): Int {
        return locationsDao.getLocationsCount()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun listenSelectedLocation(): Flow<Location?> = callbackFlow {
        val listener: LocationListener = { trySend(it) }
        selectedLocationListeners.add(listener)
        awaitClose { selectedLocationListeners.remove(listener) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun listenAddLocation(): Flow<Location> = callbackFlow {
        val listener: LocationListener = { it?.let { trySend(it) } }
        addLocationListeners.add(listener)
        awaitClose { addLocationListeners.remove(listener) }
    }
}