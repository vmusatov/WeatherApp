package com.example.weatherapp.domain.model

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
}
