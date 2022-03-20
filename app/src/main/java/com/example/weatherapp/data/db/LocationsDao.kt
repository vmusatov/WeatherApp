package com.example.weatherapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locations")
    fun getLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE is_selected = 1")
    fun getSelectedLocation(): LocationEntity?

    @Query("SELECT * FROM locations WHERE url = :url")
    fun getLocationByUrl(url: String): LocationEntity?

    @Query("SELECT COUNT(*) FROM locations")
    fun getLocationsCount(): Int

    @Insert(entity = LocationEntity::class)
    fun addLocation(locationEntity: LocationEntity)

    @Query("DELETE FROM locations WHERE url = :url")
    fun removeByUrl(url: String)

    @Update(entity = LocationEntity::class)
    fun updateLocation(locationEntity: LocationEntity)

    @Query("UPDATE locations SET position = :position WHERE url = :locationUrl")
    fun updatePosition(locationUrl: String, position: Int)
}