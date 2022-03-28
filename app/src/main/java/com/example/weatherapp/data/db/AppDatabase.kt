package com.example.weatherapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 5,
    entities = [
        LocationEntity::class
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getLocationsDao(): LocationsDao

}