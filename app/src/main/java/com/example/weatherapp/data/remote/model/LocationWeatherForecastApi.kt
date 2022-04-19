package com.example.weatherapp.data.remote.model

import com.example.weatherapp.data.utils.parseDaysToHoursForecast
import com.example.weatherapp.domain.model.Day
import com.example.weatherapp.domain.model.Hour
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.util.DateUtils
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.math.min

data class LocationWeatherForecastApi(
    val location: LocationApi,
    val current: WeatherCurrentApi,
    val forecast: ForecastApi,
) {
    fun toWeatherData(): WeatherData {
        val location = location.toLocation()
        val current = current.toCurrentWeather()
        val days = forecast.forecastDays.map { it.toDay() }
        val hours = parseDaysToHoursForecast(location.localtime, days)
        val lastUpdated = DateUtils.UPDATED_AT_DATE_FORMAT.format(Date())

        return WeatherData(location, current, hours, days, lastUpdated)
    }
}

data class ForecastApi(
    @SerializedName("forecastday")
    val forecastDays: List<ForecastDayApi>
)

data class ForecastDayApi(
    val date: String,
    val day: DayApi,
    @SerializedName("hour")
    val hours: List<HourApi>
) {
    fun toDay(): Day {
        return Day(
            date = date,
            humidity = day.humidity,
            maxTempC = day.maxTempC,
            maxTempF = day.maxTempF,
            minTempC = day.minTempC,
            minTempF = day.minTempF,
            conditionFirstIcon = hours[min(11, hours.size)].condition.icon,
            conditionSecondIcon = hours[min(20, hours.size)].condition.icon,
            hours = hours.map { it.toHour() }
        )
    }
}

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
) {
    fun toHour(): Hour {
        return Hour(
            dateTime = time,
            tempC = tempC,
            tempF = tempF,
            conditionText = condition.text,
            conditionIcon = condition.icon,
            willItRain = willItRain,
            chanceOfRain = chanceOfRain,
            willItShow = willItShow,
            chanceOfSnow = chanceOfSnow,
            humidity = humidity
        )
    }
}