package com.example.weatherapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.weatherapp.data.db.*
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApp : Application() {
    private lateinit var retrofit: Retrofit

    private lateinit var db: AppDatabase

    private lateinit var weatherApi: WeatherApi

    lateinit var weatherRepository: WeatherRepository
    lateinit var locationRepository: LocationRepository

    private lateinit var appPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        appPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        createDb()
        configureRetrofit()
        configurePicasso()

        weatherApi = retrofit.create(WeatherApi::class.java)

        weatherRepository = WeatherRepository(appPreferences, weatherApi)
        locationRepository = LocationRepository(weatherApi, db.getLocationsDao())
    }

    private fun createDb() {
        db = Room.databaseBuilder(this, AppDatabase::class.java, "database.db")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5
            )
            .build()
    }

    private fun configureRetrofit() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun configurePicasso() {
        val built = Picasso.Builder(this).build()

        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = false

        Picasso.setSingletonInstance(built)
    }

    companion object {
        const val APP_PREFERENCES = "APP_PREFERENCES"
    }
}