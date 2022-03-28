package com.example.weatherapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.weatherapp.data.db.entity.CurrentWeatherEntity

@Dao
interface CurrentWeatherDao {

    @Update(entity = CurrentWeatherEntity::class)
    suspend fun update(entity: CurrentWeatherEntity)

    @Query("UPDATE current_weather SET sunrise = :sunrise, sunset = :sunset WHERE location_id = :locationId")
    suspend fun updateAstronomy(locationId: Int, sunrise: String, sunset: String)

    @Insert(entity = CurrentWeatherEntity::class)
    suspend fun insert(entity: CurrentWeatherEntity)

    @Query("DELETE FROM current_weather WHERE location_id = :locationId")
    suspend fun deleteByLocationId(locationId: Int)
}