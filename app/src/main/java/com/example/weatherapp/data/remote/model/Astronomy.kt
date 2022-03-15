package com.example.weatherapp.data.remote.model

data class Astronomy(
    val location: Location,
    val astronomy: AstronomyData
)

data class AstronomyData(
    val astro: Astro
)

data class Astro(
    val sunrise: String,
    val sunset: String
)
