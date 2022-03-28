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
import java.util.*

class LocationRepository(
    private val weatherApi: WeatherApi,
    private val locationsDao: LocationsDao
) {

    private val disposeBag = CompositeDisposable()

    suspend fun addLocation(location: Location) {
        locationsDao.addLocation(location.toEntity())
    }

    suspend fun getSelectedLocation(): Location? {
        return locationsDao.getSelectedLocation()?.let { Location.from(it) }
    }

    suspend fun getAllLocations(): List<Location> {
        return locationsDao.getAllLocations().map { Location.from(it) }
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

    suspend fun setLocationIsSelected(location: Location) {
        locationsDao.getLocationByUrl(location.url)?.let { new ->
            locationsDao.getSelectedLocation()?.let { old ->
                old.isSelected = 0
                locationsDao.updateLocation(old)
            }

            new.isSelected = 1
            locationsDao.updateLocation(new)
        }
    }

    suspend fun removeLocation(location: Location) {
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

    suspend fun updatePosition(locationUrl: String, position: Int) {
        locationsDao.updatePosition(locationUrl, position)
    }

    suspend fun updateLocalTime(locationUrl: String, localtime: String) {
        locationsDao.updateLocaltime(locationUrl, localtime)
    }

    suspend fun setLastUpdatedIsNow(locationUrl: String) {
        locationsDao.updateLastUpdated(locationUrl, DateUtils.dateTimeToString(Date()))
    }

    suspend fun getLocationsCount(): Int {
        return locationsDao.getLocationsCount()
    }

    fun clear() = disposeBag.clear()
}