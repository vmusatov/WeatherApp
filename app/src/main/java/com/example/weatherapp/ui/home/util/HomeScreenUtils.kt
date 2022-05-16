package com.example.weatherapp.ui.home.util

import android.content.Context
import com.example.weatherapp.R

fun parseUvIndex(context: Context, index: Int): String {
    return when (index) {
        in 1..2 -> context.getString(R.string.low)
        in 3..5 -> context.getString(R.string.moderate)
        in 6..7 -> context.getString(R.string.high)
        in 8..10 -> context.getString(R.string.very_high)
        in 11..15 -> context.getString(R.string.extreme)
        else -> context.getString(R.string.undefined)
    }
}

fun parseEpaIndex(context: Context, index: Int): String {
    return when (index) {
        1 -> context.getString(R.string.good)
        2 -> context.getString(R.string.moderate)
        3 -> context.getString(R.string.unhealthy_for_sensitive_group)
        4 -> context.getString(R.string.unhealthy)
        5 -> context.getString(R.string.very_unhealthy)
        6 -> context.getString(R.string.hazardous)
        else -> context.getString(R.string.undefined)
    }
}