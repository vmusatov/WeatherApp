package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.db.entity.LocationEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.utils.safeApiCall
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.utils.WorkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationsRepositoryImpl @Inject constructor(
    private val locationsDao: LocationsDao,
    private val weatherApi: WeatherApi
) : LocationsRepository {

    override suspend fun saveLocation(location: Location): Long = withContext(Dispatchers.IO) {
        locationsDao.insert(LocationEntity.from(location))
    }

    override suspend fun deleteLocation(location: Location) = withContext(Dispatchers.IO) {
        locationsDao.deleteByUrl(location.url)
    }

    override suspend fun getSelectedLocation(): Location? = withContext(Dispatchers.IO) {
        locationsDao.getSelectedLocation()?.toLocation()
    }

    override suspend fun getAllLocations(): List<Location> = withContext(Dispatchers.IO) {
        locationsDao.getAllLocations().map { it.toLocation() }
    }

    override suspend fun autocompleteLocationsByName(name: String): WorkResult<List<Location>> =
        withContext(Dispatchers.IO) {
            safeApiCall { weatherApi.getSearchResult(name) }.map { locations ->
                locations.map { it.toLocation() }
            }
        }

    override suspend fun getLocationByUrl(url: String): Location? = withContext(Dispatchers.IO) {
        locationsDao.getLocationByUrl(url)?.toLocation()
    }

    override suspend fun updateLocationPosition(location: Location, position: Int) =
        withContext(Dispatchers.IO) {
            locationsDao.updatePosition(location.url, position)
        }

    override suspend fun updateLocationLocalTime(locationUrl: String, localtime: String) =
        withContext(Dispatchers.IO) {
            locationsDao.updateLocaltime(locationUrl, localtime)
        }

    override suspend fun setLocationIsSelected(location: Location): Unit =
        withContext(Dispatchers.IO) {
            locationsDao.getLocationByUrl(location.url)?.let {
                it.isSelected = 1
                locationsDao.update(it)
            }
        }

    override suspend fun setLocationIsNotSelected(location: Location): Unit =
        withContext(Dispatchers.IO) {
            locationsDao.getLocationByUrl(location.url)?.let {
                it.isSelected = 0
                locationsDao.update(it)
            }
        }

    override suspend fun getLocationsCount(): Int = withContext(Dispatchers.IO) {
        locationsDao.getLocationsCount()
    }
}