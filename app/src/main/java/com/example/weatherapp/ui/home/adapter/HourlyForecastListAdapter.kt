package com.example.weatherapp.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemHourlyForecastBinding
import com.example.weatherapp.model.Hour
import com.example.weatherapp.model.TempUnit
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class HoursDiffCallback(
    private val oldData: List<Hour>,
    private val newData: List<Hour>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].dateTime == newData[newItemPosition].dateTime
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData[oldItemPosition]
        val newItem = newData[newItemPosition]

        return oldItem.tempF == newItem.tempF
                && oldItem.tempC == newItem.tempC
                && oldItem.conditionIcon == newItem.conditionIcon
                && oldItem.humidity == newItem.humidity
    }
}

@SuppressLint("NotifyDataSetChanged")
class HourlyForecastListAdapter : RecyclerView.Adapter<HourlyForecastListAdapter.ViewHolder>() {

    private var data: List<Hour> = emptyList()
    private var tempUnit: TempUnit = TempUnit.DEFAULT

    fun update(data: List<Hour>, tempUnit: TempUnit) {
        if (this.tempUnit != tempUnit) {
            this.data = data
            this.tempUnit = tempUnit
            notifyDataSetChanged()
        } else {
            val diffCallback = HoursDiffCallback(this.data, data)
            val diffResult = DiffUtil.calculateDiff(diffCallback, false)

            this.data = data
            this.tempUnit = tempUnit

            diffResult.dispatchUpdatesTo(this)
        }
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

            hour.text = item.dateTime.substring(item.dateTime.indexOf(" "))
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