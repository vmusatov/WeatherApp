package com.example.weatherapp.data.remote.model

import com.example.weatherapp.domain.model.Astronomy

data class AstronomyApi(
    val location: LocationApi,
    val astronomy: AstronomyData
) {
    fun toAstronomy(): Astronomy {
        return Astronomy(
            astronomy.astro.sunrise,
            astronomy.astro.sunset
        )
    }
}

data class AstronomyData(
    val astro: Astro
)

data class Astro(
    val sunrise: String,
    val sunset: String
)
