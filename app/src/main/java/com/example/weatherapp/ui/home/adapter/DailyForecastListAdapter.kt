package com.example.weatherapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemDailyForecastBinding
import com.example.weatherapp.domain.model.Day
import com.example.weatherapp.domain.model.TempUnit

class DailyForecastListAdapter : RecyclerView.Adapter<DailyForecastItemHolder>() {

    private var data: List<Day> = emptyList()
    private var tempUnit: TempUnit = TempUnit.DEFAULT

    fun update(data: List<Day>, tempUnit: TempUnit) {
        this.data = data
        this.tempUnit = tempUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDailyForecastBinding.inflate(inflater, parent, false)
        return DailyForecastItemHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastItemHolder, position: Int) {
        holder.bind(data[position], tempUnit)
    }

    override fun getItemCount(): Int = data.size
}

