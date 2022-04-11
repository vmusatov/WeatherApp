package com.example.weatherapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDailyForecastBinding
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.model.Day
import com.example.weatherapp.util.DateUtils
import com.squareup.picasso.Picasso
import kotlin.math.min
import kotlin.math.roundToInt

class DailyForecastListAdapter : RecyclerView.Adapter<DailyForecastListAdapter.ViewHolder>() {

    private var data: List<Day> = emptyList()
    private var tempUnit: TempUnit = TempUnit.DEFAULT

    fun update(data: List<Day>, tempUnit: TempUnit) {
        this.data = data
        this.tempUnit = tempUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDailyForecastBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = data[position]
            val res = holder.itemView.resources

            day.text = res.getString(DateUtils.getDayName(item.date))

            if (tempUnit == TempUnit.C) {
                tempMax.text = res.getString(R.string.degree, item.maxTempC.roundToInt())
                tempMin.text = res.getString(R.string.degree, item.minTempC.roundToInt())
            } else {
                tempMax.text = res.getString(R.string.degree, item.maxTempF.roundToInt())
                tempMin.text = res.getString(R.string.degree, item.minTempF.roundToInt())
            }

            humidityValue.text = "${item.humidity}%"

            Picasso.get()
                .load(item.hours[min(12, item.hours.size)].conditionIcon)
                .into(conditionFrom)

            Picasso.get()
                .load(item.hours[min(21, item.hours.size)].conditionIcon)
                .into(conditionTo)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: ItemDailyForecastBinding) :
        RecyclerView.ViewHolder(binding.root)
}