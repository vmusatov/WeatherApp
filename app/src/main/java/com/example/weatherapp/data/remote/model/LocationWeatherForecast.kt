package com.example.weatherapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocationWeatherForecast(
    val location: Location,
    val current: WeatherCurrent,
    val forecast: Forecast,
    val alerts: Alerts
)

data class Alerts(
    val alert: List<Alert>
)

data class Alert(
    val desc: String,
    val event: String
)

data class Forecast(
    @SerializedName("forecastday")
    val forecastDays: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    @SerializedName("hour")
    val hours: List<Hour>
)

data class Day(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,

    @SerializedName("maxtemp_f")
    val maxTempF: Double,

    @SerializedName("mintemp_c")
    val minTempC: Double,

    @SerializedName("" + "mintemp_f")
    val minTempF: Double,

    @SerializedName("condition")
    val condition: Condition,

    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int,

    @SerializedName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int,

    @SerializedName("avghumidity")
    val humidity: Int
)

data class Hour(
    @SerializedName("time")
    val time: String,

    @SerializedName("temp_c")
    val tempC: Double,

    @SerializedName("temp_f")
    val tempF: Double,

    @SerializedName("condition")
    val condition: Condition,

    @SerializedName("will_it_rain")
    val willItRain: Int,

    @SerializedName("chance_of_rain")
    val chanceOfRain: Int,

    @SerializedName("chance_of_snow")
    val chanceOfSnow: Int,

    @SerializedName("will_it_snow")
    val willItShow: Int,

    @SerializedName("humidity")
    val humidity: Int
) {
    fun isRain(): Boolean =
        willItRain != 0 || chanceOfRain > 50 || condition.text.lowercase().contains("rain")

    fun isSnow(): Boolean =
        willItShow != 0 || chanceOfSnow > 50 || condition.text.lowercase().contains("snow")

    fun isHavePrecipitation(): Boolean = isRain() || isSnow()

    fun isNotHavePrecipitation(): Boolean = !isHavePrecipitation()
}