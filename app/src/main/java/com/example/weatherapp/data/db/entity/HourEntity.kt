package com.example.weatherapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.weatherapp.domain.model.Hour

@Entity(
    tableName = "hours",
    indices = [Index(value = ["location_id"])]
)
data class HourEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "location_id")
    var locationId: Int,

    @ColumnInfo(name = "day_id")
    var dayId: Int,

    @ColumnInfo(name = "date_time")
    var dateTime: String,

    @ColumnInfo(name = "temp_c")
    var tempC: Double,

    @ColumnInfo(name = "temp_f")
    var tempF: Double,

    @ColumnInfo(name = "condition_text")
    var conditionText: String,

    @ColumnInfo(name = "condition_icon")
    var conditionIcon: String,

    @ColumnInfo(name = "will_it_rain")
    var willItRain: Int,

    @ColumnInfo(name = "chance_of_rain")
    var chanceOfRain: Int,

    @ColumnInfo(name = "chance_of_snow")
    var chanceOfSnow: Int,

    @ColumnInfo(name = "will_it_snow")
    var willItShow: Int,

    @ColumnInfo(name = "humidity")
    var humidity: Int
) {
    fun toHour(): Hour {
        return Hour(
            dateTime = dateTime,
            tempC = tempC,
            tempF = tempF,
            conditionText = conditionText,
            conditionIcon = conditionIcon,
            willItRain = willItRain,
            chanceOfRain = chanceOfRain,
            willItShow = willItShow,
            chanceOfSnow = chanceOfSnow,
            humidity = humidity
        )
    }

    companion object {
        fun from(from: Hour): HourEntity {
            return HourEntity(
                id = 0,
                locationId = -1,
                dayId = -1,
                dateTime = from.dateTime,
                tempC = from.tempC,
                tempF = from.tempF,
                conditionText = from.conditionText,
                conditionIcon = from.conditionIcon,
                willItRain = from.willItRain,
                chanceOfRain = from.chanceOfRain,
                willItShow = from.willItShow,
                chanceOfSnow = from.chanceOfSnow,
                humidity = from.humidity
            )
        }
    }
}