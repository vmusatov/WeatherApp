package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.LocationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationsRepositoryImpl @Inject constructor(
    private val locationsDao: LocationsDao,
    private val weatherApi: WeatherApi
) : LocationsRepository {

    override suspend fun saveLocation(location: Location): Long = withContext(Dispatchers.IO) {
        locationsDao.insert(location.toEntity())
    }

    override suspend fun deleteLocation(location: Location) = withContext(Dispatchers.IO) {
        locationsDao.deleteByUrl(location.url)
    }

    override suspend fun getSelectedLocation(): Location? = withContext(Dispatchers.IO) {
        locationsDao.getSelectedLocation()?.let { Location.from(it) }
    }

    override suspend fun getAllLocations(): List<Location> = withContext(Dispatchers.IO) {
        locationsDao.getAllLocations().map { Location.from(it) }
    }

    override suspend fun getLocationsByName(name: String): List<Location> =
        withContext(Dispatchers.IO) {
            try {
                weatherApi.getSearchResult(name).blockingGet().map { Location.from(it) }
            } catch (e: Exception) {
                emptyList()
            }
        }

    override suspend fun getLocationByUrl(url: String): Location? = withContext(Dispatchers.IO) {
        locationsDao.getLocationByUrl(url)?.let {
            return@withContext Location.from(it)
        }
        return@withContext null
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
            location.isSelected = true
            locationsDao.update(location.toEntity())
        }

    override suspend fun setLocationIsNotSelected(location: Location): Unit =
        withContext(Dispatchers.IO) {
            location.isSelected = false
            locationsDao.update(location.toEntity())
        }

    override suspend fun getLocationsCount(): Int = withContext(Dispatchers.IO) {
        locationsDao.getLocationsCount()
    }
}