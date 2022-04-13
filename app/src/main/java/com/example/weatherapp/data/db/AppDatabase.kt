package com.example.weatherapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.db.dao.*
import com.example.weatherapp.data.db.entity.CurrentWeatherEntity
import com.example.weatherapp.data.db.entity.DayEntity
import com.example.weatherapp.data.db.entity.HourEntity
import com.example.weatherapp.data.db.entity.LocationEntity

@Database(
    version = 6,
    entities = [
        LocationEntity::class,
        CurrentWeatherEntity::class,
        DayEntity::class,
        HourEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getLocationsDao(): LocationsDao

    abstract fun getCurrentWeatherDao(): CurrentWeatherDao

    abstract fun getDaysDao(): DaysDao

    abstract fun getHoursDao(): HoursDao

    abstract fun getWeatherDao(): WeatherDao
}