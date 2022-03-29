package com.example.weatherapp.repository

import android.content.SharedPreferences
import com.example.weatherapp.data.db.dao.CurrentWeatherDao
import com.example.weatherapp.data.db.dao.DaysDao
import com.example.weatherapp.data.db.dao.HoursDao
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.model.AstronomyApi
import com.example.weatherapp.data.remote.model.LocationWeatherCurrentApi
import com.example.weatherapp.data.remote.model.LocationWeatherForecastApi
import com.example.weatherapp.model.*
import com.example.weatherapp.ui.settings.SettingsFragment
import com.example.weatherapp.util.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class WeatherRepository(
    private val appPreferences: SharedPreferences,
    private val weatherApi: WeatherApi,
    private val currentWeatherDao: CurrentWeatherDao,
    private val daysDao: DaysDao,
    private val hoursDao: HoursDao
) {

    private val disposeBag = CompositeDisposable()

    fun loadForecast(
        q: String,
        onSuccess: Consumer<LocationWeatherForecastApi>,
        onError: Consumer<Throwable>
    ) {
        val result = weatherApi.getForecast(q)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    suspend fun loadLocationsCurrentWeather(locations: List<String>): List<LocationWeatherCurrentApi>? {
        return withContext(Dispatchers.IO) {
            locations.map {
                try {
                    weatherApi.getCurrent(it).blockingGet()
                } catch (e: Throwable) {
                    return@withContext null
                }
            }
        }
    }

    fun loadAstronomy(
        q: String,
        onSuccess: Consumer<AstronomyApi>,
        onError: Consumer<Throwable>
    ) {
        val result = weatherApi.getAstronomy(q, DateUtils.dateFormat.format(Date()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    fun getTempUnit(): TempUnit {
        return TempUnit.fromCode(
            appPreferences.getInt(SettingsFragment.PREF_TEMP_CODE, TempUnit.DEFAULT.code)
        ) ?: TempUnit.DEFAULT
    }

    fun saveTempUnit(tempUnit: TempUnit) {
        appPreferences.edit()
            .putInt(SettingsFragment.PREF_TEMP_CODE, tempUnit.code)
            .apply()
    }

    suspend fun updateAstronomy(locationId: Int, astronomy: Astronomy) =
        withContext(Dispatchers.IO) {
            currentWeatherDao.updateAstronomy(locationId, astronomy.sunrise, astronomy.sunset)
        }

    suspend fun deleteLocationData(locationId: Int) = withContext(Dispatchers.IO) {
        currentWeatherDao.deleteByLocationId(locationId)
        daysDao.deleteByLocationId(locationId)
        hoursDao.deleteByLocationId(locationId)
    }

    suspend fun addCurrentWeather(locationId: Int, current: CurrentWeather) =
        withContext(Dispatchers.IO) {
            val entity = current.toEntity()
            entity.locationId = locationId
            currentWeatherDao.insert(entity)
        }

    suspend fun addHour(locationId: Int, dayId: Int, hour: Hour) = withContext(Dispatchers.IO) {
        val entity = hour.toEntity()
        entity.locationId = locationId
        entity.dayId = dayId
        hoursDao.insert(entity)
    }

    suspend fun addDay(locationId: Int, day: Day): Long = withContext(Dispatchers.IO) {
        val entity = day.toEntity()
        entity.locationId = locationId
        return@withContext daysDao.insert(entity)
    }

    fun clear() {
        disposeBag.clear()
    }
}
