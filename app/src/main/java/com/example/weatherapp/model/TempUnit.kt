package com.example.weatherapp.model

enum class TempUnit(
    val unitName: String,
    val code: Int
) {
    C("°C", 0),
    F("°F", 1);

    fun invert(): TempUnit {
        return if (this == C) F else C
    }

    companion object {
        val DEFAULT = C

        fun fromCode(code: Int): TempUnit? {
            return values().firstOrNull { unit -> unit.code == code }
        }
    }
}