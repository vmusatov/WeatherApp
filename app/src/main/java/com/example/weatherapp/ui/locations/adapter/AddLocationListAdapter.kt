package com.example.weatherapp.ui.locations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemLocationSearchBinding
import com.example.weatherapp.domain.model.Location

typealias AddLocationListener = (location: Location) -> Unit

class AddLocationListAdapter(
    private val addLocationListener: AddLocationListener
) : RecyclerView.Adapter<AddLocationItemHolder>() {

    private var data: List<Location> = listOf()

    fun update(data: List<Location>) {
        val newData = data.sortedBy { it.position }.toMutableList()
        val diffCallback = LocationsDiffCallback(this.data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback, false)

        this.data = newData

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddLocationItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationSearchBinding.inflate(inflater, parent, false)
        return AddLocationItemHolder(binding, addLocationListener)
    }

    override fun onBindViewHolder(holder: AddLocationItemHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
