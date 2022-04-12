package com.example.weatherapp.data.di

import android.content.SharedPreferences
import com.example.weatherapp.data.db.dao.CurrentWeatherDao
import com.example.weatherapp.data.db.dao.DaysDao
import com.example.weatherapp.data.db.dao.HoursDao
import com.example.weatherapp.data.db.dao.LocationsDao
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.repository.LocationsRepositoryImpl
import com.example.weatherapp.data.repository.SettingsRepositoryImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.repository.SettingsRepository
import com.example.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideSettingsRepository(appPreferences: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(appPreferences)
    }

    @Provides
    fun provideLocationsRepository(
        locationsDao: LocationsDao,
        weatherApi: WeatherApi
    ): LocationsRepository {
        return LocationsRepositoryImpl(locationsDao, weatherApi)
    }

    @Provides
    fun provideWeatherRepository(
        weatherApi: WeatherApi,
        locationsDao: LocationsDao,
        currentWeatherDao: CurrentWeatherDao,
        daysDao: DaysDao,
        hoursDao: HoursDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi, locationsDao, currentWeatherDao, daysDao, hoursDao)
    }
}