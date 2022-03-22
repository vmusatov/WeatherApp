package com.example.weatherapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 4,
    entities = [
        LocationEntity::class
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getLocationsDao(): LocationsDao

}