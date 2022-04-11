package com.example.weatherapp.domain.model

import com.example.weatherapp.data.db.entity.HourEntity
import com.example.weatherapp.data.remote.model.HourApi

data class Hour(
    var dateTime: String,
    var tempC: Double,
    var tempF: Double,
    var conditionText: String,
    var conditionIcon: String,
    var willItRain: Int,
    var chanceOfRain: Int,
    var chanceOfSnow: Int,
    var willItShow: Int,
    var humidity: Int
) {
    fun isRain(): Boolean = willItRain != 0
            || (chanceOfRain > 50 && chanceOfRain > chanceOfSnow)
            || conditionText.lowercase().contains("rain")

    fun isSnow(): Boolean = willItShow != 0
            || (chanceOfSnow > 50 && chanceOfSnow > chanceOfRain)
            || conditionText.lowercase().contains("snow")

    fun isHavePrecipitation(): Boolean = isRain() || isSnow()

    fun isNotHavePrecipitation(): Boolean = !isHavePrecipitation()

    fun toEntity(): HourEntity {
        return HourEntity(
            id = 0,
            locationId = -1,
            dayId = -1,
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
        fun from(from: HourEntity): Hour {
            return Hour(
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

        fun from(from: HourApi): Hour {
            return Hour(
                dateTime = from.time,
                tempC = from.tempC,
                tempF = from.tempF,
                conditionText = from.condition.text,
                conditionIcon = from.condition.icon,
                willItRain = from.willItRain,
                chanceOfRain = from.chanceOfRain,
                willItShow = from.willItShow,
                chanceOfSnow = from.chanceOfSnow,
                humidity = from.humidity
            )
        }
    }
}
