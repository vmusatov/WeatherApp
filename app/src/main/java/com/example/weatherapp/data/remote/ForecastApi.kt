package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.model.WeatherForecast
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {

    @GET("forecast.json")
    fun getForecast(
        @Query("q") q: String,
        @Query("days") days: Int = 3,
        @Query("key") key: String = "25cae335814e483c8c4132054221502",
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes"
    ): Single<WeatherForecast>
}