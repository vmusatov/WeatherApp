package com.example.weatherapp

import android.app.Application
import android.content.Context
import com.example.weatherapp.di.AppComponent
import com.example.weatherapp.di.DaggerAppComponent
import com.squareup.picasso.Picasso

val Context.appComponent: AppComponent
    get() = when (this) {
        is WeatherApp -> appComponent
        else -> applicationContext.appComponent
    }

class WeatherApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()

        configurePicasso()
    }

    private fun configurePicasso() {
        val built = Picasso.Builder(this).build()

        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = false

        Picasso.setSingletonInstance(built)
    }
}