package com.example.weatherapp.model

import com.example.weatherapp.data.remote.model.AstronomyApi

data class Astronomy(
    val sunrise: String,
    val sunset: String
) {
    companion object {
        fun from(from: AstronomyApi): Astronomy {
            return Astronomy(
                from.astronomy.astro.sunrise,
                from.astronomy.astro.sunset
            )
        }
    }
}