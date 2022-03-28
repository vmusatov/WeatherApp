package com.example.weatherapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.data.db.entity.HourEntity

@Dao
interface HoursDao {

    @Insert(entity = HourEntity::class)
    suspend fun insert(entity: HourEntity)

    @Query("DELETE FROM hours WHERE location_id = :locationId")
    suspend fun deleteByLocationId(locationId: Int)
}