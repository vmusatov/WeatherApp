package com.example.weatherapp.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    companion object {
        private const val APP_PREFERENCES = "APP_PREFERENCES"
    }
}