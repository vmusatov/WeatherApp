package com.example.weatherapp.ui.locations.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemManageLocationsBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.ui.locations.util.LocationsChangeCallback

@SuppressLint("NotifyDataSetChanged")
class ManageLocationListAdapter(
    private val locationsChangeCallback: LocationsChangeCallback,
) : RecyclerView.Adapter<ManageLocationItemHolder>() {

    private var locations: MutableList<Location> = mutableListOf()
    private var weatherData: MutableList<ShortWeatherData> = mutableListOf()
    var tempUnit: TempUnit = TempUnit.DEFAULT

    private var isEditMode = false
    private var selectedItems: MutableList<Location> = mutableListOf()

    fun updateLocationsInfo(weatherData: List<ShortWeatherData>) {
        weatherData.forEach { newItem ->
            val oldItem = this.weatherData.firstOrNull { it.locationName == newItem.locationName }

            if (oldItem == null) {
                this.weatherData.add(newItem)
            } else if (oldItem != newItem) {
                this.weatherData[this.weatherData.indexOf(oldItem)] = newItem
            }

            val changedItemId = locations.indexOfFirst { it.name == newItem.locationName }
            notifyItemChanged(changedItemId)
        }
    }

    fun update(locations: List<Location>) {
        val newData = locations.sortedBy { it.position }.toMutableList()
        val diffCallback = LocationsDiffCallback(this.locations, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.locations = newData

        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteSelected() {
        selectedItems.forEach {
            notifyItemRemoved(locations.indexOf(it))
            locations.remove(it)
        }
        locationsChangeCallback.onDeleteLocations(selectedItems)
        selectedItems.clear()
    }

    fun switchEditMode(selectedItem: Location? = null) {
        if (isEditMode) {
            locationsChangeCallback.onApplyChanges(locations)
        }
        isEditMode = !isEditMode

        selectedItems.clear()
        selectedItem?.let { selectedItems.add(it) }

        locationsChangeCallback.onSwitchEditMode(isEditMode)

        notifyDataSetChanged()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev: Location = locations.removeAt(fromPosition)
        locations.add(toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageLocationItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemManageLocationsBinding.inflate(inflater, parent, false)
        return ManageLocationItemHolder(
            binding = binding,
            isEditMode = { isEditMode },
            onSwitchEditMode = { item -> switchEditMode(item) },
            onAddItem = { item -> selectedItems.add(item) },
            onRemoveItem = { item -> selectedItems.remove(item) },
            isItemSelected = { item -> selectedItems.contains(item) }
        )
    }

    override fun onBindViewHolder(holder: ManageLocationItemHolder, position: Int) {
        val item = locations[position]
        val itemWeatherData = weatherData.firstOrNull { it.locationName == item.name }

        holder.bindUi(item, itemWeatherData, tempUnit)
        holder.bindListeners(item, locationsChangeCallback)
    }

    override fun getItemCount(): Int {
        return locations.size
    }
}
