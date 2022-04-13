package com.example.weatherapp.domain.model

data class Day(
    var date: String,
    var humidity: Int,
    var maxTempC: Double,
    var maxTempF: Double,
    var minTempC: Double,
    var minTempF: Double,
    var conditionFirstIcon: String,
    var conditionSecondIcon: String,
    var hours: List<Hour>
)