package com.example.weatherapp.data.repository

import android.content.SharedPreferences
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val appPreferences: SharedPreferences,
    private val externalScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : SettingsRepository {

    override suspend fun getTempUnit(): TempUnit = withContext(dispatcher) {
        TempUnit.fromCode(
            appPreferences.getInt(PREF_TEMP_CODE, TempUnit.DEFAULT.code)
        ) ?: TempUnit.DEFAULT
    }

    override suspend fun saveTempUnit(tempUnit: TempUnit) {
        externalScope.launch(dispatcher) {
            appPreferences.edit()
                .putInt(PREF_TEMP_CODE, tempUnit.code)
                .apply()
        }.join()
    }

    companion object {
        const val PREF_TEMP_CODE = "PREF_TEMP_CODE"
    }
}