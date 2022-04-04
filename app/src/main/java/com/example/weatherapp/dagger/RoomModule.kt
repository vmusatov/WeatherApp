package com.example.weatherapp.dagger

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.data.db.*
import com.example.weatherapp.data.db.dao.CurrentWeatherDao
import com.example.weatherapp.data.db.dao.DaysDao
import com.example.weatherapp.data.db.dao.HoursDao
import com.example.weatherapp.data.db.dao.LocationsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Provides
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6
            )
            .build()
    }

    @Provides
    fun provideCurrentWeatherDao(db: AppDatabase): CurrentWeatherDao = db.getCurrentWeatherDao()

    @Provides
    fun provideDaysDao(db: AppDatabase): DaysDao = db.getDaysDao()

    @Provides
    fun provideHoursDao(db: AppDatabase): HoursDao = db.getHoursDao()

    @Provides
    fun provideLocationsDao(db: AppDatabase): LocationsDao = db.getLocationsDao()

    companion object {
        private const val DB_NAME = "database.db"
    }
}