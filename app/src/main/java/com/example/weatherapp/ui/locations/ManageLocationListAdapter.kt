package com.example.weatherapp.ui.locations

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
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.LocationWeatherInfo
import com.example.weatherapp.model.TempUnit
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

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

@SuppressLint("NotifyDataSetChanged")
class ManageLocationListAdapter(
    private val locationsChangeCallback: LocationsChangeCallback,
) : RecyclerView.Adapter<ManageLocationListAdapter.ViewHolder>() {

    private var data: MutableList<Location> = mutableListOf()
    private var dataInfo: MutableList<LocationWeatherInfo> = mutableListOf()
    var tempUnit: TempUnit = TempUnit.DEFAULT

    private var isEditMode = false
    private var selectedItems: MutableList<Location> = mutableListOf()

    fun updateLocationsInfo(info: List<LocationWeatherInfo>) {
        info.forEach { newItem ->
            val oldItem = this.dataInfo.firstOrNull { it.locationName == newItem.locationName }

            if (oldItem == null) {
                dataInfo.add(newItem)
            } else if (oldItem != newItem) {
                dataInfo[dataInfo.indexOf(oldItem)] = newItem
            }

            val changedItemId = data.indexOfFirst { it.name == newItem.locationName }
            notifyItemChanged(changedItemId)
        }
    }

    fun update(data: List<Location>) {
        val newData = data.sortedBy { it.position }.toMutableList()
        val diffCallback = LocationsDiffCallback(this.data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.data = newData

        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteSelected() {
        selectedItems.forEach {
            notifyItemRemoved(data.indexOf(it))
            data.remove(it)
        }
        locationsChangeCallback.onDeleteLocations(selectedItems)
        selectedItems.clear()
    }

    fun switchEditMode(selectedItem: Location? = null) {
        if (isEditMode) {
            locationsChangeCallback.onApplyChanges(data)
        }
        isEditMode = !isEditMode

        selectedItems.clear()
        selectedItem?.let { selectedItems.add(it) }

        locationsChangeCallback.onSwitchEditMode(isEditMode)

        notifyDataSetChanged()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev: Location = data.removeAt(fromPosition)
        data.add(toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemManageLocationsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
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
            dataInfo.firstOrNull { it.locationName == item.name }?.let {
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
        return data.size
    }

    class ViewHolder(val binding: ItemManageLocationsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
