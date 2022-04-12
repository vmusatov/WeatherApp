package com.example.weatherapp.domain.usecase.weather

import com.example.weatherapp.R
import com.example.weatherapp.domain.usecase.BaseUseCase
import javax.inject.Inject

class ParseUvIndexUseCase @Inject constructor(): BaseUseCase<Int, Int>() {
    override suspend fun execute(data: Int): Int {
        return when (data) {
            in 1..2 -> R.string.low
            in 3..5 -> R.string.moderate
            in 6..7 -> R.string.high
            in 8..10 -> R.string.very_high
            in 11..15 -> R.string.extreme
            else -> R.string.undefined
        }
    }
}