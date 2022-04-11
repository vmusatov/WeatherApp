package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.TempUnit

interface SettingsRepository {

    suspend fun getTempUnit(): TempUnit

    suspend fun saveTempUnit(tempUnit: TempUnit)
}