package com.example.weatherapp.ui.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemLocationSearchBinding
import com.example.weatherapp.model.LocationDto

typealias AddLocationListener = (location: LocationDto) -> Unit

class AddLocationListAdapter(
    private val addLocationListener: AddLocationListener
) : RecyclerView.Adapter<AddLocationListAdapter.ViewHolder>(), View.OnClickListener {

    private var data: MutableList<LocationDto> = mutableListOf()

    fun update(data: List<LocationDto>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
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
        val location = v.tag as LocationDto
        addLocationListener.invoke(location)
    }
}
