package com.example.weatherapp.data.remote.model

data class ConditionApi(
    val text: String,
    val code: Int
) {
    var icon: String = ""
        get() {
            val replaced = field.replaceFirst("64x64", "128x128")
            return if (!replaced.contains("http")) {
                "https:$replaced"
            } else {
                replaced
            }
        }
}
