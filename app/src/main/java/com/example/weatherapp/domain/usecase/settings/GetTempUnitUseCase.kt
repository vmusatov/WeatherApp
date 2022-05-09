package com.example.weatherapp.domain.usecase.settings

import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.repository.SettingsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class GetTempUnitUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseUseCase<Unit, TempUnit> {

    override suspend fun execute(data: Unit): TempUnit {
        return settingsRepository.getTempUnit()
    }
}