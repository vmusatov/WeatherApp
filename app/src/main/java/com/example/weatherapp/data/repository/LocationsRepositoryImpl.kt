package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.db.entity.LocationEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.utils.safeApiCall
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.utils.WorkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LocationsRepositoryImpl(
    private val locationsDao: LocationsDao,
    private val weatherApi: WeatherApi,
    private val externalScope: CoroutineScope
) : LocationsRepository {

    override suspend fun saveLocation(location: Location): Long =
        externalScope.async {
            locationsDao.insert(LocationEntity.from(location))
        }.await()

    override suspend fun deleteLocation(location: Location) {
        externalScope.launch { locationsDao.deleteByUrl(location.url) }.join()
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
}