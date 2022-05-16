package com.example.weatherapp.data.repository

import android.content.SharedPreferences
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.model.TempUnitListener
import com.example.weatherapp.domain.repository.SettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepositoryImpl(
    private val appPreferences: SharedPreferences,
    private val externalScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : SettingsRepository {

    private val tempUnitListeners = mutableSetOf<TempUnitListener>()

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

        tempUnitListeners.forEach { it(tempUnit) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun listenTempUnit(): Flow<TempUnit> = callbackFlow {
        val listener: TempUnitListener = { trySend(it) }
        tempUnitListeners.add(listener)
        awaitClose { tempUnitListeners.remove(listener) }
    }

    companion object {
        const val PREF_TEMP_CODE = "PREF_TEMP_CODE"
    }
}