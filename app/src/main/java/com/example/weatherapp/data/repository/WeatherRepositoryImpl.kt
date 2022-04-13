package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.*
import com.example.weatherapp.data.db.entity.CurrentWeatherEntity
import com.example.weatherapp.data.db.entity.DayEntity
import com.example.weatherapp.data.db.entity.HourEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.utils.safeApiCall
import com.example.weatherapp.data.utils.safeDbCall
import com.example.weatherapp.domain.model.*
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.utils.WorkResult
import com.example.weatherapp.domain.utils.WorkResult.Success
import com.example.weatherapp.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao,
    private val locationsDao: LocationsDao,
    private val currentWeatherDao: CurrentWeatherDao,
    private val daysDao: DaysDao,
    private val hoursDao: HoursDao
) : WeatherRepository {

    override suspend fun getWeatherDataByLocation(
        forceLoad: Boolean,
        location: Location
    ): WorkResult<WeatherData> = withContext(Dispatchers.IO) {
        if (forceLoad) {
            val weatherDataResult = loadWeatherData(location)
            if (weatherDataResult is Success) {
                saveWeatherData(location, weatherDataResult.data)
            }
            weatherDataResult
        } else {
            getWeatherDataFromDb(location)
        }
    }

    private suspend fun getWeatherDataFromDb(location: Location): WorkResult<WeatherData> =
        withContext(Dispatchers.IO) {
            safeDbCall { weatherDao.getLocationWeatherByUrl(location.url) }.map { it.toWeatherData() }
        }

    private suspend fun loadWeatherData(location: Location): WorkResult<WeatherData> =
        withContext(Dispatchers.IO) {
            val result =
                safeApiCall { weatherApi.getForecast(location.url) }.map { it.toWeatherData() }

            if (result is Success) { loadAstronomy(location, result.data) }

            result
        }

    private suspend fun loadAstronomy(location: Location, weatherData: WeatherData) =
        withContext(Dispatchers.IO) {
            val query = "${location.lat}, ${location.lon}"
            val dateNow = DateUtils.DATE_FORMAT.format(Date())

            val result =
                safeApiCall { weatherApi.getAstronomy(query, dateNow) }.map { it.toAstronomy() }

            if (result is Success) {
                weatherData.current.astronomy = result.data
            }
        }

    override suspend fun getShortWeatherInfo(location: Location): WorkResult<ShortWeatherInfo> =
        withContext(Dispatchers.IO) {
            safeApiCall { weatherApi.getCurrent(location.url) }
                .map { it.toShortWeatherInfo() }
        }

    override suspend fun clearWeatherData(location: Location): Unit =
        withContext(Dispatchers.IO) {
            val locationId = getLocationId(location)

            locationId?.let {
                currentWeatherDao.deleteByLocationId(it)
                daysDao.deleteByLocationId(it)
                hoursDao.deleteByLocationId(it)
            }
        }

    private suspend fun saveWeatherData(location: Location, data: WeatherData): Unit =
        withContext(Dispatchers.IO) {
            getLocationId(location)?.let { locationId ->
                clearWeatherData(location)

                locationsDao.updateLocaltime(location.url, data.location.localtime)

                addCurrentWeather(locationId, data.current)
                data.daysForecast.forEach { day ->
                    val dayId = addDay(locationId, day)
                    day.hours.forEach { addHour(locationId, dayId.toInt(), it) }
                }

                locationsDao.updateLastUpdated(location.url, DateUtils.dateTimeToString(Date()))
            }
        }

    private suspend fun addCurrentWeather(locationId: Int, current: CurrentWeather) =
        withContext(Dispatchers.IO) {
            val entity = CurrentWeatherEntity.from(current)
            entity.locationId = locationId
            currentWeatherDao.insert(entity)
        }

    private suspend fun addHour(locationId: Int, dayId: Int, hour: Hour) =
        withContext(Dispatchers.IO) {
            val entity = HourEntity.from(hour)
            entity.locationId = locationId
            entity.dayId = dayId
            hoursDao.insert(entity)
        }

    private suspend fun addDay(locationId: Int, day: Day): Long = withContext(Dispatchers.IO) {
        val entity = DayEntity.from(day)
        entity.locationId = locationId
        return@withContext daysDao.insert(entity)
    }

    private suspend fun getLocationId(location: Location): Int? {
        return locationsDao.getLocationByUrl(location.url)?.id
    }
}