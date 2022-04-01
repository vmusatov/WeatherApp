package com.example.weatherapp.repository

import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.db.entity.LocationWithWeatherTuple
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.model.SearchLocationApi
import com.example.weatherapp.model.Location
import com.example.weatherapp.util.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val locationsDao: LocationsDao
) {

    private val disposeBag = CompositeDisposable()

    suspend fun getLocationWithWeather(locationUrl: String): LocationWithWeatherTuple? =
        withContext(Dispatchers.IO) {
            try {
                locationsDao.getLocationByUrlWithWeather(locationUrl)
            } catch (e: NullPointerException) {
                null
            }
        }

    suspend fun addLocation(location: Location) = withContext(Dispatchers.IO) {
        location.position = locationsDao.getLocationsCount()

        if (location.position == 0) {
            location.isSelected = true
        }

        locationsDao.insert(location.toEntity())
    }

    suspend fun getLocationByUrl(url: String): Location? = withContext(Dispatchers.IO) {
        locationsDao.getLocationByUrl(url)?.let {
            return@withContext Location.from(it)
        }
        return@withContext null
    }

    suspend fun getSelectedLocation(): Location? = withContext(Dispatchers.IO) {
        locationsDao.getSelectedLocation()?.let { Location.from(it) }
    }

    suspend fun getAllLocations(): List<Location> = withContext(Dispatchers.IO) {
        locationsDao.getAllLocations().map { Location.from(it) }
    }

    fun loadSearchAutocomplete(
        q: String,
        onSuccess: Consumer<List<SearchLocationApi>>,
        onError: Consumer<Throwable>
    ) {
        val result = weatherApi.getSearchResult(q)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    suspend fun setLocationIsSelected(location: Location) = withContext(Dispatchers.IO) {
        locationsDao.getLocationByUrl(location.url)?.let { new ->
            locationsDao.getSelectedLocation()?.let { old ->
                old.isSelected = 0
                locationsDao.update(old)
            }

            new.isSelected = 1
            locationsDao.update(new)
        }
    }

    suspend fun deleteLocation(location: Location) = withContext(Dispatchers.IO) {
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

    suspend fun updatePosition(locationUrl: String, position: Int) = withContext(Dispatchers.IO) {
        locationsDao.updatePosition(locationUrl, position)
    }

    suspend fun updateLocalTime(locationUrl: String, localtime: String) =
        withContext(Dispatchers.IO) {
            locationsDao.updateLocaltime(locationUrl, localtime)
        }

    suspend fun setLastUpdatedIsNow(locationUrl: String) = withContext(Dispatchers.IO) {
        locationsDao.updateLastUpdated(locationUrl, DateUtils.dateTimeToString(Date()))
    }

    fun clear() = disposeBag.clear()
}