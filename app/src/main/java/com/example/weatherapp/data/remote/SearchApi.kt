package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.model.SearchLocation
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search.json")
    fun getSearchResult(
        @Query("q") q: String,
        @Query("key") key: String = "25cae335814e483c8c4132054221502",
    ): Single<List<SearchLocation>>
}