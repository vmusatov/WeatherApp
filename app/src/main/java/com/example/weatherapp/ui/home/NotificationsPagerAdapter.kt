package com.example.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemWeatherNotificationBinding
import com.example.weatherapp.notification.WeatherNotification

class NotificationsPagerAdapter : RecyclerView.Adapter<NotificationsPagerAdapter.ViewHolder>() {

    private var data: List<WeatherNotification> = listOf()

    fun update (data: List<WeatherNotification>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeatherNotificationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.run {
        with(holder.binding) {
            val item = data[position]

            title.text = item.title
            message.text = item.message
        }
    }

    inner class ViewHolder(val binding: ItemWeatherNotificationBinding) : RecyclerView.ViewHolder(binding.root)
}
