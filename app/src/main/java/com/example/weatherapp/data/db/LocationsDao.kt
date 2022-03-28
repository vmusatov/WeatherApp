package com.example.weatherapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE is_selected = 1")
    suspend fun getSelectedLocation(): LocationEntity?

    @Query("SELECT * FROM locations WHERE url = :url")
    suspend fun getLocationByUrl(url: String): LocationEntity?

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getLocationsCount(): Int

    @Insert(entity = LocationEntity::class)
    suspend fun addLocation(locationEntity: LocationEntity)

    @Query("DELETE FROM locations WHERE url = :url")
    suspend fun removeByUrl(url: String)

    @Update(entity = LocationEntity::class)
    suspend fun updateLocation(locationEntity: LocationEntity)

    @Query("UPDATE locations SET position = :position WHERE url = :locationUrl")
    suspend fun updatePosition(locationUrl: String, position: Int)

    @Query("UPDATE locations SET last_updated = :dateTime WHERE url = :locationUrl")
    suspend fun updateLastUpdated(locationUrl: String, dateTime: String)

    @Query("UPDATE locations SET localtime = :localtime WHERE url = :locationUrl")
    suspend fun updateLocaltime(locationUrl: String, localtime: String)
}