package com.example.weatherapp.ui.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDailyForecastBinding
import com.example.weatherapp.domain.model.Day
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.util.DateUtils
import com.squareup.picasso.Picasso
import kotlin.math.min
import kotlin.math.roundToInt

class DailyForecastItemHolder(
    private val binding: ItemDailyForecastBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Day, tempUnit: TempUnit) = with(binding) {
        day.text = itemView.resources.getString(DateUtils.getDayName(data.date))
        humidityValue.text = "${data.humidity}%"

        bindTemp(data, tempUnit)
        bindIcons(data)
    }

    private fun bindTemp(data: Day, tempUnit: TempUnit) = with(binding) {
        val res = itemView.resources

        if (tempUnit == TempUnit.C) {
            tempMax.text = res.getString(R.string.degree, data.maxTempC.roundToInt())
            tempMin.text = res.getString(R.string.degree, data.minTempC.roundToInt())
        } else {
            tempMax.text = res.getString(R.string.degree, data.maxTempF.roundToInt())
            tempMin.text = res.getString(R.string.degree, data.minTempF.roundToInt())
        }
    }

    private fun bindIcons(data: Day) = with(binding) {
        Picasso.get()
            .load(data.hours[min(12, data.hours.size)].conditionIcon)
            .into(conditionFrom)

        Picasso.get()
            .load(data.hours[min(21, data.hours.size)].conditionIcon)
            .into(conditionTo)
    }
}