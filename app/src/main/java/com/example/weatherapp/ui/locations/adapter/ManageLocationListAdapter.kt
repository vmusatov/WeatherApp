package com.example.weatherapp.ui.locations.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemManageLocationsBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.ui.locations.util.LocationsChangeCallback
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

@SuppressLint("NotifyDataSetChanged")
class ManageLocationListAdapter(
    private val locationsChangeCallback: LocationsChangeCallback,
) : RecyclerView.Adapter<ManageLocationListAdapter.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemManageLocationsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = locations[position]
        setupItemUi(holder, item)
        setupItemListeners(holder, item)
    }

    private fun setupItemUi(holder: ViewHolder, item: Location) {
        with(holder.binding) {
            root.tag = item

            if (isEditMode) {
                checkbox.visibility = View.VISIBLE
                checkbox.isChecked = selectedItems.contains(item)
                dragDropView.visibility = View.VISIBLE
                locationTemp.visibility = View.GONE
                locationCondition.visibility = View.GONE
            } else {
                checkbox.visibility = View.GONE
                dragDropView.visibility = View.GONE
                locationTemp.visibility = View.VISIBLE
                locationCondition.visibility = View.VISIBLE
            }

            locationName.text = item.name
            locationDesc.text = "${item.region}, ${item.country}"

            val res = holder.itemView.resources
            weatherData.firstOrNull { it.locationName == item.name }?.let {
                locationTemp.text = if (tempUnit == TempUnit.C) {
                    res.getString(R.string.degree, it.tempC.roundToInt())
                } else {
                    res.getString(R.string.degree, it.tempF.roundToInt())
                }
                Picasso.get()
                    .load(it.conditionIconUrl)
                    .into(locationCondition)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupItemListeners(holder: ViewHolder, item: Location) {
        with(holder.binding) {
            root.setOnClickListener {
                if (!isEditMode) {
                    locationsChangeCallback.onSelectLocation(it.tag as Location)
                } else {
                    checkbox.performClick()
                }
            }

            root.setOnLongClickListener {
                switchEditMode(item)
                return@setOnLongClickListener true
            }

            dragDropView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    locationsChangeCallback.onDragStart(holder)
                }
                return@setOnTouchListener true
            }

            checkbox.setOnClickListener {
                if ((it as CheckBox).isChecked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.remove(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    class ViewHolder(val binding: ItemManageLocationsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
