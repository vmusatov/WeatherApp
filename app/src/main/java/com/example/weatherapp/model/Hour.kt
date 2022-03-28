package com.example.weatherapp.model

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

    companion object {
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
