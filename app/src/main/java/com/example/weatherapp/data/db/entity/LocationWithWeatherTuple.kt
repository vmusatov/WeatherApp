package com.example.weatherapp.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

class LocationWithWeatherTuple(
    @Embedded
    val location: LocationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val current: CurrentWeatherEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val hours: List<HourEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val days: List<DayEntity>
)