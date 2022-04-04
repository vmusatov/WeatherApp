package com.example.weatherapp.ui.locations.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.Location

class LocationsDiffCallback(
    private val oldData: List<Location>,
    private val newData: List<Location>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].url == newData[newItemPosition].url
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}