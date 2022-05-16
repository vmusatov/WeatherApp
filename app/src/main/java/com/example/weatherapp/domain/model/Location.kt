package com.example.weatherapp.domain.model

import java.util.Date

typealias LocationListener = (Location?) -> Unit

data class Location(
    var name: String,
    var region: String,
    var country: String,
    var localtime: String,
    var lat: Double,
    var lon: Double,
    var url: String,
    var isSelected: Boolean = false,
    var position: Int = -1,
    var lastUpdated: Date? = null
)