package com.example.weatherapp.domain.usecase.settings

import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.repository.SettingsRepository
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class SaveTempUnitUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseUseCase<TempUnit, Unit>() {

    override suspend fun execute(data: TempUnit) {
        settingsRepository.saveTempUnit(data)
    }
}