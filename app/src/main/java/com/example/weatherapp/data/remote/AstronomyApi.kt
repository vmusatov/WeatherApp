package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.model.Astronomy
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface AstronomyApi {

    @GET("astronomy.json")
    fun getAstronomy(
        @Query("q") q: String,
        @Query("dt") dt: String,
        @Query("key") key: String = "25cae335814e483c8c4132054221502",
    ): Single<Astronomy>
}