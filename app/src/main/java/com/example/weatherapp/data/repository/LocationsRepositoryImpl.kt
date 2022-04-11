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
        location.position = locationsDao.getLocationsCount()

        if (location.position == 0) {
            location.isSelected = true
        }
        locationsDao.insert(location.toEntity())
    }

    override suspend fun deleteLocation(location: Location) = withContext(Dispatchers.IO) {
        locationsDao.getLocationByUrl(location.url)?.let { entity ->
            if (entity.isSelected == 1) {
                val notSelectedLocation =
                    locationsDao.getAllLocations().firstOrNull { it.isSelected == 0 }

                notSelectedLocation?.let {
                    it.isSelected = 1
                    locationsDao.update(it)
                }
            }
        }

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
            weatherApi.getSearchResult(name).blockingGet().map { Location.from(it) }
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
            locationsDao.getLocationByUrl(location.url)?.let { new ->
                locationsDao.getSelectedLocation()?.let { old ->
                    old.isSelected = 0
                    locationsDao.update(old)
                }

                new.isSelected = 1
                locationsDao.update(new)
            }
        }
}