package com.example.weatherapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocationWeatherForecastApi(
    val location: LocationApi,
    val current: WeatherCurrentApi,
    val forecast: ForecastApi,
)

data class ForecastApi(
    @SerializedName("forecastday")
    val forecastDays: List<ForecastDayApi>
)

data class ForecastDayApi(
    val date: String,
    val day: DayApi,
    @SerializedName("hour")
    val hours: List<HourApi>
)

data class DayApi(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,

    @SerializedName("maxtemp_f")
    val maxTempF: Double,

    @SerializedName("mintemp_c")
    val minTempC: Double,

    @SerializedName("" + "mintemp_f")
    val minTempF: Double,

    @SerializedName("condition")
    val condition: ConditionApi,

    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int,

    @SerializedName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int,

    @SerializedName("avghumidity")
    val humidity: Int
)

data class HourApi(
    @SerializedName("time")
    val time: String,

    @SerializedName("temp_c")
    val tempC: Double,

    @SerializedName("temp_f")
    val tempF: Double,

    @SerializedName("condition")
    val condition: ConditionApi,

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
)