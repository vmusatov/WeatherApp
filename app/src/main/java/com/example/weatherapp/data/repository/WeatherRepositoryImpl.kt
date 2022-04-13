package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.*
import com.example.weatherapp.data.db.entity.CurrentWeatherEntity
import com.example.weatherapp.data.db.entity.DayEntity
import com.example.weatherapp.data.db.entity.HourEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.model.*
import com.example.weatherapp.domain.repository.WeatherRepository
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
    ): WeatherData? = withContext(Dispatchers.IO) {

        if (forceLoad) {
            val weatherData = loadWeatherData(location)
            weatherData?.let { saveWeatherData(location, weatherData) }

            weatherData
        } else {
            getWeatherDataFromDb(location)
        }
    }

    private suspend fun getWeatherDataFromDb(location: Location): WeatherData? =
        withContext(Dispatchers.IO) {
            try {
                weatherDao.getLocationWeatherByUrl(location.url)?.toWeatherData()
            } catch (e: Exception) {
                null
            }
        }

    private suspend fun loadWeatherData(location: Location): WeatherData? =
        withContext(Dispatchers.IO) {
            try {
                val forecast = weatherApi.getForecast(location.url).blockingGet()
                val weatherData = forecast.toWeatherData()

                loadAstronomy(location)?.let { weatherData.current.astronomy = it }

                weatherData
            } catch (e: Exception) {
                null
            }
        }

    private suspend fun loadAstronomy(location: Location): Astronomy? =
        withContext(Dispatchers.IO) {
            try {
                val query = "${location.lat}, ${location.lon}"
                val fromApi =
                    weatherApi.getAstronomy(query, DateUtils.DATE_FORMAT.format(Date()))
                        .blockingGet()

                fromApi.toAstronomy()
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun getShortWeatherInfo(location: Location): ShortWeatherInfo? =
        withContext(Dispatchers.IO) {
            try {
                val currentWeather = weatherApi.getCurrent(location.url).blockingGet()
                currentWeather.toShortWeatherInfo()
            } catch (e: Exception) {
                null
            }
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