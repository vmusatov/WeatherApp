package com.example.weatherapp.ui.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemHourlyForecastBinding
import com.example.weatherapp.domain.model.Hour
import com.example.weatherapp.domain.model.TempUnit
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class HourlyForecastItemHolder(
    private val binding: ItemHourlyForecastBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Hour, tempUnit: TempUnit) = with(binding) {
        root.tag = data
        hour.text = data.dateTime.substring(data.dateTime.indexOf(" "))
        humidityValue.text = "${data.humidity}%"

        bindTemp(data, tempUnit)
        bindIcon(data)
    }

    private fun bindTemp(data: Hour, tempUnit: TempUnit) = with(binding) {
        val res = itemView.resources

        hourTemp.text = if (tempUnit == TempUnit.C) {
            res.getString(R.string.degree, data.tempC.roundToInt())
        } else {
            res.getString(R.string.degree, data.tempF.roundToInt())
        }
    }

    private fun bindIcon(data: Hour) = with(binding) {
        Picasso.get()
            .load(data.conditionIcon)
            .into(conditionIcon)
    }
}