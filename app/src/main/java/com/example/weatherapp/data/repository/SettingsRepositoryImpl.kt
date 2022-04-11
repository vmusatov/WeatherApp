package com.example.weatherapp.data.repository

import android.content.SharedPreferences
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.repository.SettingsRepository
import com.example.weatherapp.ui.settings.SettingsFragment
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val appPreferences: SharedPreferences,
): SettingsRepository {

    override suspend fun getTempUnit(): TempUnit {
        return TempUnit.fromCode(
            appPreferences.getInt(SettingsFragment.PREF_TEMP_CODE, TempUnit.DEFAULT.code)
        ) ?: TempUnit.DEFAULT
    }

    override suspend fun saveTempUnit(tempUnit: TempUnit) {
        appPreferences.edit()
            .putInt(SettingsFragment.PREF_TEMP_CODE, tempUnit.code)
            .apply()
    }
}