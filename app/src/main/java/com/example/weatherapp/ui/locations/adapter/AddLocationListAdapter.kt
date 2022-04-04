package com.example.weatherapp.ui.locations.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemLocationSearchBinding
import com.example.weatherapp.model.Location

typealias AddLocationListener = (location: Location) -> Unit

class AddLocationListAdapter(
    private val addLocationListener: AddLocationListener
) : RecyclerView.Adapter<AddLocationListAdapter.ViewHolder>(), View.OnClickListener {

    private var data: List<Location> = listOf()

    fun update(data: List<Location>) {
        val newData = data.sortedBy { it.position }.toMutableList()
        val diffCallback = LocationsDiffCallback(this.data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback, false)

        this.data = newData

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationSearchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = data[position]
            root.tag = item

            locationName.text = item.name
            locationDesc.text = "${item.region}, ${item.country}"

            root.setOnClickListener(this@AddLocationListAdapter)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(val binding: ItemLocationSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View) {
        val location = v.tag as Location
        addLocationListener.invoke(location)
    }
}
