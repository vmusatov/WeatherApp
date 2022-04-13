package com.example.weatherapp.data.remote

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.model.AstronomyApi
import com.example.weatherapp.data.remote.model.LocationWeatherCurrentApi
import com.example.weatherapp.data.remote.model.LocationWeatherForecastApi
import com.example.weatherapp.data.remote.model.SearchLocationApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    private val KEY: String
        get() = BuildConfig.weatherApiKey

    @GET("current.json")
    suspend fun getCurrent(
        @Query("q") q: String,
        @Query("aqi") aqi: String = "yes",
        @Query("key") key: String = KEY,
    ): Response<LocationWeatherCurrentApi>

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") q: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes",
        @Query("key") key: String = KEY
    ): Response<LocationWeatherForecastApi>

    @GET("astronomy.json")
    suspend fun getAstronomy(
        @Query("q") q: String,
        @Query("dt") dt: String,
        @Query("key") key: String = KEY,
    ): Response<AstronomyApi>

    @GET("search.json")
    suspend fun getSearchResult(
        @Query("q") q: String,
        @Query("key") key: String = KEY,
    ): Response<List<SearchLocationApi>>
}