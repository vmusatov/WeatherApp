package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.dao.CurrentWeatherDao
import com.example.weatherapp.data.db.dao.DaysDao
import com.example.weatherapp.data.db.dao.HoursDao
import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherInfo
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.util.DateUtils
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val locationsDao: LocationsDao,
    private val currentWeatherDao: CurrentWeatherDao,
    private val daysDao: DaysDao,
    private val hoursDao: HoursDao
) : WeatherRepository {

    override suspend fun getWeatherDataByLocation(
        forceLoad: Boolean,
        location: Location
    ): WeatherData {
        val dbData = locationsDao.getLocationByUrlWithWeather(location.url)?.let {
            WeatherData.from(it)
        }

        return if (forceLoad || needForceLoad(location) || dbData == null) {
            WeatherData.from(weatherApi.getForecast(location.url).blockingGet())
        } else {
            dbData
        }
    }

    override suspend fun getShortWeatherInfo(location: Location): ShortWeatherInfo {
        return ShortWeatherInfo.from(weatherApi.getCurrent(location.url).blockingGet())
    }

    private fun needForceLoad(location: Location): Boolean {
        if (location.lastUpdated == null) {
            return true
        }

        val datesDiffInMin = DateUtils.datesDiffInMin(location.lastUpdated!!, Date())
        return datesDiffInMin > MAX_MINUTES_WITHOUT_UPDATE
    }

    companion object {
        private const val MAX_MINUTES_WITHOUT_UPDATE = 60
    }
}