package com.example.weatherapp.data.remote

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    private val KEY: String
        get() = BuildConfig.weatherApiKey

    @GET("current.json")
    fun getCurrent(
        @Query("q") q: String,
        @Query("aqi") aqi: String = "yes",
        @Query("key") key: String = KEY,
    ): Single<LocationWeatherCurrentApi>

    @GET("forecast.json")
    fun getForecast(
        @Query("q") q: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes",
        @Query("key") key: String = KEY
    ): Single<LocationWeatherForecastApi>

    @GET("astronomy.json")
    fun getAstronomy(
        @Query("q") q: String,
        @Query("dt") dt: String,
        @Query("key") key: String = KEY,
    ): Single<AstronomyApi>

    @GET("search.json")
    fun getSearchResult(
        @Query("q") q: String,
        @Query("key") key: String = KEY,
    ): Single<List<SearchLocationApi>>
}