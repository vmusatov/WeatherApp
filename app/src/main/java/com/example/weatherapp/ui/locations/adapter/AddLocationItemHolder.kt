package com.example.weatherapp.ui.locations.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemLocationSearchBinding
import com.example.weatherapp.domain.model.Location

class AddLocationItemHolder(
    private val binding: ItemLocationSearchBinding,
    private val onItemSelect: (Location) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Location) = with(binding) {
        root.tag = item

        locationName.text = item.name
        locationDesc.text = "${item.region}, ${item.country}"

        root.setOnClickListener { onItemSelect(item) }
    }
}