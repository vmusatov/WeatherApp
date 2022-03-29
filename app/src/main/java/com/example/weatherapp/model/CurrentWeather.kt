package com.example.weatherapp.model

import com.example.weatherapp.data.db.entity.CurrentWeatherEntity
import com.example.weatherapp.data.remote.model.LocationWeatherForecastApi

data class CurrentWeather(
    var tempC: Double,
    var tempF: Double,
    var feelsLikeTempC: Double,
    var feelsLikeTempF: Double,
    var conditionIcon: String,
    var conditionText: String,
    var windKph: Double,
    var uvIndex: Int,
    var astronomy: Astronomy,
    var pressureMb: Double,
    var co: Double,
    var no2: Double,
    var o3: Double,
    var so2: Double,
    var usEpaIndex: Int
) {
    fun toEntity(): CurrentWeatherEntity {
        return CurrentWeatherEntity(
            id = 0,
            locationId = -1,
            tempC = tempC,
            tempF = tempF,
            feelsLikeTempC = feelsLikeTempC,
            feelsLikeTempF = feelsLikeTempF,
            conditionIcon = conditionIcon,
            conditionText = conditionText,
            sunrise = astronomy.sunrise,
            sunset = astronomy.sunset,
            windKph = windKph,
            uvIndex = uvIndex,
            pressureMb = pressureMb,
            co = co,
            no2 = no2,
            o3 = o3,
            so2 = so2,
            usEpaIndex = usEpaIndex
        )
    }

    companion object {
        fun from(from: CurrentWeatherEntity): CurrentWeather {
            return CurrentWeather(
                tempC = from.tempC,
                tempF = from.tempF,
                feelsLikeTempC = from.feelsLikeTempC,
                feelsLikeTempF = from.feelsLikeTempF,
                conditionIcon = from.conditionIcon,
                conditionText = from.conditionText,
                astronomy = Astronomy(from.sunrise, from.sunset),
                windKph = from.windKph,
                uvIndex = from.uvIndex,
                pressureMb = from.pressureMb,
                co = from.co,
                no2 = from.no2,
                o3 = from.o3,
                so2 = from.so2,
                usEpaIndex = from.usEpaIndex
            )
        }

        fun from(from: LocationWeatherForecastApi): CurrentWeather {
            return CurrentWeather(
                tempC = from.current.tempC,
                tempF = from.current.tempF,
                feelsLikeTempC = from.current.feelsLikeTempC,
                feelsLikeTempF = from.current.feelsLikeTempF,
                conditionIcon = from.current.condition.icon,
                conditionText = from.current.condition.text,
                astronomy = Astronomy("", ""),
                windKph = from.current.windKph,
                uvIndex = from.current.uvIndex,
                pressureMb = from.current.pressureMb,
                co = from.current.airQuality.co,
                no2 = from.current.airQuality.no2,
                o3 = from.current.airQuality.o3,
                so2 = from.current.airQuality.so2,
                usEpaIndex = from.current.airQuality.usEpaIndex
            )
        }
    }
}