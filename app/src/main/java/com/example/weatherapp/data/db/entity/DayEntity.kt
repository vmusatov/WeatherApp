package com.example.weatherapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "days",
    indices = [Index(value = ["location_id"])]
)
data class DayEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "location_id")
    var locationId: Int,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "humidity")
    var humidity: Int,

    @ColumnInfo(name = "max_temp_c")
    var maxTempC: Double,

    @ColumnInfo(name = "max_temp_f")
    var maxTempF: Double,

    @ColumnInfo(name = "min_temp_c")
    var minTempC: Double,

    @ColumnInfo(name = "min_temp_f")
    var minTempF: Double,

    @ColumnInfo(name = "condition_first_icon")
    var conditionFirstIcon: String,

    @ColumnInfo(name = "condition_second_icon")
    var conditionSecondIcon: String,
)
