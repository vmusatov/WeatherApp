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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class LocationRepository(
    private val weatherApi: WeatherApi,
    private val locationsDao: LocationsDao
) {

    private val disposeBag = CompositeDisposable()

    suspend fun addLocation(location: LocationDto) {
        locationsDao.addLocation(LocationEntity.fromLocationDto(location))
    }

    suspend fun getSelectedLocation(): LocationDto? {
        return locationsDao.getSelectedLocation()?.toLocationDto()
    }

    suspend fun getAllLocations(): List<LocationDto> {
        return locationsDao.getAllLocations().map { it.toLocationDto() }
    }

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

    suspend fun setLocationIsSelected(location: LocationDto) {
        locationsDao.getLocationByUrl(location.url)?.let { new ->
            locationsDao.getSelectedLocation()?.let { old ->
                old.isSelected = 0
                locationsDao.updateLocation(old)
            }

            new.isSelected = 1
            locationsDao.updateLocation(new)
        }
    }

    suspend fun removeLocation(location: LocationDto) {
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

    suspend fun setLastUpdatedIsNow(locationUrl: String) {
        locationsDao.locationUpdated(locationUrl, DateUtils.dateTimeToString(Date()))
    }

    suspend fun getLocationsCount(): Int {
        return locationsDao.getLocationsCount()
    }

    fun clear() = disposeBag.clear()
}