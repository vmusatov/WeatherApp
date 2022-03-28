package com.example.weatherapp.repository

import com.example.weatherapp.data.db.LocationsDao
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

class LocationRepository(
    private val weatherApi: WeatherApi,
    private val locationsDao: LocationsDao
) {

    private val disposeBag = CompositeDisposable()

    suspend fun addLocation(location: Location) = withContext(Dispatchers.IO) {
        locationsDao.addLocation(location.toEntity())
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
                locationsDao.updateLocation(old)
            }

            new.isSelected = 1
            locationsDao.updateLocation(new)
        }
    }

    suspend fun removeLocation(location: Location) = withContext(Dispatchers.IO) {
        locationsDao.getLocationByUrl(location.url)?.let { entity ->
            if (entity.isSelected == 1) {
                val notSelectedLocation =
                    locationsDao.getAllLocations().firstOrNull { it.isSelected == 0 }

                notSelectedLocation?.let {
                    it.isSelected = 1
                    locationsDao.updateLocation(it)
                }
            }
        }

        locationsDao.removeByUrl(location.url)
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

    suspend fun getLocationsCount(): Int = withContext(Dispatchers.IO) {
        locationsDao.getLocationsCount()
    }

    fun clear() = disposeBag.clear()
}