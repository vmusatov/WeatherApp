package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.TempUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun getTempUnit(): TempUnit

    suspend fun saveTempUnit(tempUnit: TempUnit)

    fun listenTempUnit(): Flow<TempUnit>
}