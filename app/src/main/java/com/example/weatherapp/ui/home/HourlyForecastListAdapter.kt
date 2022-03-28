package com.example.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemHourlyForecastBinding
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.Hour
import com.example.weatherapp.util.DateUtils
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class HourlyForecastListAdapter : RecyclerView.Adapter<HourlyForecastListAdapter.ViewHolder>() {

    private var data: List<Hour> = emptyList()
    private var tempUnit: TempUnit = TempUnit.DEFAULT

    fun update(data: List<Hour>, tempUnit: TempUnit) {
        this.data = data
        this.tempUnit = tempUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHourlyForecastBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = data[position]
            val res = holder.itemView.resources
            root.tag = item

            hour.text = "${DateUtils.getHourFromDate(item.dateTime)}:00"
            hourTemp.text = if (tempUnit == TempUnit.C) {
                res.getString(R.string.degree, item.tempC.roundToInt())
            } else {
                res.getString(R.string.degree, item.tempF.roundToInt())
            }

            humidityValue.text = "${item.humidity}%"

            Picasso.get()
                .load(item.conditionIcon)
                .into(conditionIcon)
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(val binding: ItemHourlyForecastBinding) : RecyclerView.ViewHolder(binding.root)
}