package com.example.weatherapp.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE locations ADD COLUMN is_selected INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE locations ADD COLUMN position INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE locations ADD COLUMN last_updated TEXT DEFAULT NULL")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE locations ADD COLUMN localtime TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS 'current_weather' (" +
                    "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " 'location_id' INTEGER NOT NULL DEFAULT -1," +
                    " 'temp_c' REAL NOT NULL DEFAULT 0," +
                    " 'temp_f' REAL NOT NULL DEFAULT 0," +
                    " 'feels_like_c' REAL NOT NULL DEFAULT 0," +
                    " 'feels_like_f' REAL NOT NULL DEFAULT 0," +
                    " 'condition_icon' TEXT NOT NULL DEFAULT ''," +
                    " 'condition_text' TEXT NOT NULL DEFAULT ''," +
                    " 'wind_kph' REAL NOT NULL DEFAULT 0," +
                    " 'uv_index' INTEGER NOT NULL DEFAULT 0," +
                    " 'pressure_mb' REAL NOT NULL DEFAULT 0," +
                    " 'sunrise' TEXT NOT NULL DEFAULT ''," +
                    " 'sunset' TEXT NOT NULL DEFAULT ''," +
                    " 'co' REAL NOT NULL DEFAULT 0," +
                    " 'no2' REAL NOT NULL DEFAULT 0," +
                    " 'o3' REAL NOT NULL DEFAULT 0," +
                    " 'so2' REAL NOT NULL DEFAULT 0," +
                    " 'us_epa_index' INTEGER NOT NULL DEFAULT 0" +
                    ")"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS " +
                    "'index_current_weather_location_id'" +
                    " ON 'current_weather' ('location_id')"
        )

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS 'days' (" +
                    "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " 'location_id' INTEGER NOT NULL DEFAULT -1," +
                    " 'date' TEXT NOT NULL DEFAULT ''," +
                    " 'humidity' INTEGER NOT NULL DEFAULT 0," +
                    " 'max_temp_c' REAL NOT NULL DEFAULT 0," +
                    " 'max_temp_f' REAL NOT NULL DEFAULT 0," +
                    " 'min_temp_c' REAL NOT NULL DEFAULT 0," +
                    " 'min_temp_f' REAL NOT NULL DEFAULT 0," +
                    " 'condition_first_icon' TEXT NOT NULL DEFAULT ''," +
                    " 'condition_second_icon' TEXT NOT NULL DEFAULT ''" +
                    ")"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS " +
                    "'index_days_location_id' " +
                    "ON 'days' ('location_id')"
        )

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS 'hours' (" +
                    "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " 'location_id' INTEGER NOT NULL DEFAULT -1," +
                    " 'day_id' INTEGER NOT NULL DEFAULT -1," +
                    " 'date_time' TEXT NOT NULL DEFAULT ''," +
                    " 'temp_c' REAL NOT NULL DEFAULT 0," +
                    " 'temp_f' REAL NOT NULL DEFAULT 0," +
                    " 'condition_text' TEXT NOT NULL DEFAULT ''," +
                    " 'condition_icon' TEXT NOT NULL DEFAULT ''," +
                    " 'will_it_rain' INTEGER NOT NULL DEFAULT 0," +
                    " 'chance_of_rain' INTEGER NOT NULL DEFAULT 0," +
                    " 'chance_of_snow' INTEGER NOT NULL DEFAULT 0," +
                    " 'will_it_snow' INTEGER NOT NULL DEFAULT 0," +
                    " 'humidity' INTEGER NOT NULL DEFAULT 0" +
                    ")"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS" +
                    " 'index_hours_location_id'" +
                    " ON 'hours' ('location_id')"
        )
    }
}