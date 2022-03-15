package com.example.weatherapp.util

import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

        fun dateTimeFromString(s: String): Date? {
            return dateTimeFormat.parse(s)
        }

        fun dateTimeToString(d: Date): String {
            return dateTimeFormat.format(d)
        }

        fun getDayName(dateString: String): Int {
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(dateString) ?: Date()

            return mapDateName(calendar.get(Calendar.DAY_OF_WEEK))
        }

        fun getNowHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        fun getHourFromDate(dateString: String): Int {
            val calendar = Calendar.getInstance()
            calendar.time = dateTimeFormat.parse(dateString) ?: Date()

            return calendar.get(Calendar.HOUR_OF_DAY)
        }

        private fun mapDateName(dayOfWeek: Int): Int {
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return  R.string.today
            }

            return when (dayOfWeek) {
                1 -> R.string.sunday
                2 -> R.string.monday
                3 -> R.string.tuesday
                4 -> R.string.wednesday
                5 -> R.string.thursday
                6 -> R.string.friday
                7 -> R.string.saturday
                else -> R.string.undefined_day
            }
        }
    }
}