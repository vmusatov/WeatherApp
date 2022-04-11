package com.example.weatherapp.domain.model

import com.example.weatherapp.data.db.entity.DayEntity
import com.example.weatherapp.data.remote.model.ForecastDayApi
import kotlin.math.min

data class Day(
    var date: String,
    var humidity: Int,
    var maxTempC: Double,
    var maxTempF: Double,
    var minTempC: Double,
    var minTempF: Double,
    var conditionFirstIcon: String,
    var conditionSecondIcon: String,
    var hours: List<Hour>
) {
    fun toEntity(): DayEntity {
        return DayEntity(
            id = 0,
            locationId = -1,
            date = date,
            humidity = humidity,
            maxTempC = maxTempC,
            maxTempF = maxTempF,
            minTempC = minTempC,
            minTempF = minTempF,
            conditionFirstIcon = conditionFirstIcon,
            conditionSecondIcon = conditionSecondIcon
        )
    }

    companion object {
        fun from(from: DayEntity): Day {
            return Day(
                date = from.date,
                humidity = from.humidity,
                maxTempC = from.maxTempC,
                maxTempF = from.maxTempF,
                minTempC = from.minTempC,
                minTempF = from.minTempF,
                conditionFirstIcon = from.conditionFirstIcon,
                conditionSecondIcon = from.conditionSecondIcon,
                emptyList()
            )
        }

        fun from(from: ForecastDayApi): Day {
            return Day(
                date = from.date,
                humidity = from.day.humidity,
                maxTempC = from.day.maxTempC,
                maxTempF = from.day.maxTempF,
                minTempC = from.day.minTempC,
                minTempF = from.day.minTempF,
                conditionFirstIcon = from
                    .hours[min(11, from.hours.size)]
                    .condition
                    .icon,
                conditionSecondIcon = from
                    .hours[min(20, from.hours.size)]
                    .condition
                    .icon,
                hours = from.hours.map { Hour.from(it) }
            )
        }
    }
}