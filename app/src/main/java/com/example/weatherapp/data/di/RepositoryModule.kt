package com.example.weatherapp.data.di

import android.content.SharedPreferences
import com.example.weatherapp.data.db.dao.*
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.repository.LocationsRepositoryImpl
import com.example.weatherapp.data.repository.SettingsRepositoryImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.di.DefaultDispatcher
import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.domain.repository.LocationsRepository
import com.example.weatherapp.domain.repository.SettingsRepository
import com.example.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        appPreferences: SharedPreferences,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        externalScope: CoroutineScope
    ): SettingsRepository {
        return SettingsRepositoryImpl(appPreferences, externalScope, dispatcher)
    }

    @Provides
    @Singleton
    fun provideLocationsRepository(
        locationsDao: LocationsDao,
        weatherApi: WeatherApi,
        externalScope: CoroutineScope
    ): LocationsRepository {
        return LocationsRepositoryImpl(locationsDao, weatherApi, externalScope)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApi: WeatherApi,
        weatherDao: WeatherDao,
        locationsDao: LocationsDao,
        currentWeatherDao: CurrentWeatherDao,
        daysDao: DaysDao,
        hoursDao: HoursDao,
        @DefaultDispatcher dispatcher: CoroutineDispatcher,
        externalScope: CoroutineScope
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            weatherApi,
            weatherDao,
            locationsDao,
            currentWeatherDao,
            daysDao,
            hoursDao,
            externalScope,
            dispatcher
        )
    }
}