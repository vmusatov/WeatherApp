package com.example.weatherapp.repository

import com.example.weatherapp.data.db.LocationEntity
import com.example.weatherapp.data.db.LocationsDao
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.model.SearchLocation
import com.example.weatherapp.model.LocationDto
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

    fun loadSearchAutocomplete(
        q: String,
        onSuccess: Consumer<List<SearchLocation>>,
        onError: Consumer<Throwable>
    ) {
        val result = weatherApi.getSearchResult(q)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    fun getSelectedLocation(): LocationDto? {
        return locationsDao.getSelectedLocation()?.toLocationDto()
    }

    fun getLocations(): List<LocationDto> {
        return locationsDao.getLocations().map { it.toLocationDto() }
    }

    fun setLocationIsSelected(location: LocationDto) {
        val new = locationsDao.getLocationByUrl(location.url)
        new?.let {
            val old = locationsDao.getSelectedLocation()
            old?.let {
                it.isSelected = 0
                locationsDao.updateLocation(it)
            }

            it.isSelected = 1
            locationsDao.updateLocation(it)
        }
    }

    fun addLocation(location: LocationDto) {
        locationsDao.addLocation(LocationEntity.fromLocationDto(location))
    }

    fun removeLocation(location: LocationDto) {
        val fromDb = locationsDao.getLocationByUrl(location.url)
        fromDb?.let {
            if (it.isSelected == 1) {
                val lastLocations = locationsDao.getLocations().first { it.isSelected == 0 }

                lastLocations.isSelected = 1
                locationsDao.updateLocation(lastLocations)
            }
        }

        locationsDao.removeByUrl(location.url)
    }

    fun updatePosition(locationUrl: String, position: Int) {
        locationsDao.updatePosition(locationUrl, position)
    }

    fun setLastUpdatedIsNow(locationUrl: String) {
        locationsDao.locationUpdated(locationUrl, DateUtils.dateTimeToString(Date()))
    }

    fun getLocationsCount(): Int {
        return locationsDao.getLocationsCount()
    }

    fun clear() {
        disposeBag.clear()
    }
}