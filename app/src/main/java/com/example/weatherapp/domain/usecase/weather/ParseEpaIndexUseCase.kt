package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.R
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class ParseEpaIndexUseCase @Inject constructor() : BaseUseCase<Int, Int> {
    override suspend fun execute(data: Int): Int {
        return when (data) {
            1 -> R.string.good
            2 -> R.string.moderate
            3 -> R.string.unhealthy_for_sensitive_group
            4 -> R.string.unhealthy
            5 -> R.string.very_unhealthy
            6 -> R.string.hazardous
            else -> R.string.undefined
        }
    }
}