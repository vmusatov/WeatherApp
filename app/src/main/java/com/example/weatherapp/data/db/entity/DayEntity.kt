package com.example.weatherapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.weatherapp.domain.model.Day

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
) {
    fun toDay(): Day {
        return Day(
            date = date,
            humidity = humidity,
            maxTempC = maxTempC,
            maxTempF = maxTempF,
            minTempC = minTempC,
            minTempF = minTempF,
            conditionFirstIcon = conditionFirstIcon,
            conditionSecondIcon = conditionSecondIcon,
            emptyList()
        )
    }
    companion object {
        fun from(from: Day): DayEntity {
            return DayEntity(
                id = 0,
                locationId = -1,
                date = from.date,
                humidity = from.humidity,
                maxTempC = from.maxTempC,
                maxTempF = from.maxTempF,
                minTempC = from.minTempC,
                minTempF = from.minTempF,
                conditionFirstIcon = from.conditionFirstIcon,
                conditionSecondIcon = from.conditionSecondIcon
            )
        }
    }
}
