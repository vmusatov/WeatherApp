package com.example.weatherapp.domain.usecase.settings

import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.repository.SettingsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenTempUnitUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseUseCase<Unit, Flow<TempUnit>> {

    override suspend fun execute(data: Unit): Flow<TempUnit> {
        return settingsRepository.listenTempUnit()
    }
}