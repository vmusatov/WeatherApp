package com.example.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.model.ForecastDay
import com.example.weatherapp.databinding.ItemByDayBinding
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.util.DateUtils
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class DailyForecastListAdapter : RecyclerView.Adapter<DailyForecastListAdapter.ViewHolder>() {

    private var data: List<ForecastDay> = emptyList()
    private var tempUnit: TempUnit = TempUnit.DEFAULT

    fun update(data: List<ForecastDay>, tempUnit: TempUnit) {
        this.data = data
        this.tempUnit = tempUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemByDayBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = data[position]
            val res = holder.itemView.resources

            day.text = res.getString(DateUtils.getDayName(item.date))

            if (tempUnit == TempUnit.C) {
                tempMax.text = res.getString(R.string.degree, item.day.maxTempC.roundToInt())
                tempMin.text = res.getString(R.string.degree, item.day.minTempC.roundToInt())
            } else {
                tempMax.text = res.getString(R.string.degree, item.day.maxTempF.roundToInt())
                tempMin.text = res.getString(R.string.degree, item.day.minTempF.roundToInt())
            }

            humidityValue.text = "${item.day.humidity}%"

            Picasso.get()
                .load(item.hour[12].condition.icon)
                .into(conditionFrom)

            Picasso.get()
                .load(item.hour[21].condition.icon)
                .into(conditionTo)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: ItemByDayBinding) : RecyclerView.ViewHolder(binding.root)
}