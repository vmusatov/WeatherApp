package com.example.weatherapp.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.weatherapp.data.db.entity.LocationWeatherTuple

@Dao
interface WeatherDao {

    @Transaction
    @Query("SELECT * FROM locations WHERE url = :url")
    suspend fun getLocationWeatherByUrl(url: String): LocationWeatherTuple?
}