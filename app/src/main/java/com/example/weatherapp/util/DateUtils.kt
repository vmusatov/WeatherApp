package com.example.weatherapp.util

import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtils {
    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val DATE_TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val UPDATED_AT_DATE_FORMAT = SimpleDateFormat("dd.MM, HH:mm")

        fun dateTimeFromString(s: String): Date? {
            if (s.isEmpty()) {
                return null
            }
            return DATE_TIME_FORMAT.parse(s)
        }

        fun dateTimeToString(d: Date): String {
            return DATE_TIME_FORMAT.format(d)
        }

        fun getDayName(dateString: String): Int {
            val calendar = Calendar.getInstance()
            calendar.time = DATE_FORMAT.parse(dateString) ?: Date()

            return mapDateName(calendar.get(Calendar.DAY_OF_WEEK))
        }

        fun getHourFromDate(dateString: String): Int {
            val calendar = Calendar.getInstance()
            calendar.time = DATE_TIME_FORMAT.parse(dateString) ?: Date()

            return calendar.get(Calendar.HOUR_OF_DAY)
        }

        fun datesDiffInMin(startDate: Date, endDate: Date): Long {
            val duration = endDate.time - startDate.time
            return TimeUnit.MILLISECONDS.toMinutes(duration)
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