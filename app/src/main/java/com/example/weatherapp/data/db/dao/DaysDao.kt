package com.example.weatherapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.data.db.entity.DayEntity

@Dao
interface DaysDao {
    @Query("DELETE FROM days WHERE location_id = :locationId")
    suspend fun deleteByLocationId(locationId: Int)

    @Insert(entity = DayEntity::class)
    suspend fun insert(entity: DayEntity): Long
}