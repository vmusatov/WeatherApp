package com.example.weatherapp.ui.locations

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemManageLocationsBinding
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.model.TempUnit
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

data class LocationInfo(
    val locationName: String,
    val tempC: Double,
    val tempF: Double,
    val conditionIconUrl: String
)

class ManageLocationListAdapter(
    private val locationsManager: LocationsManager,
) : RecyclerView.Adapter<ManageLocationListAdapter.ViewHolder>() {

    private var data: MutableList<LocationDto> = mutableListOf()
    private var dataInfo: MutableList<LocationInfo> = mutableListOf()
    var tempUnit: TempUnit = TempUnit.DEFAULT

    private var isEditMode = false
    private var selectedItems: MutableList<LocationDto> = mutableListOf()

    fun updateLocationsInfo(info: List<LocationInfo>) {
        dataInfo = info.toMutableList()
        notifyDataSetChanged()
    }

    fun update(data: List<LocationDto>) {
        this.data = data.sortedBy { it.position }.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteSelected() {
        selectedItems.forEach {
            notifyItemRemoved(data.indexOf(it))
            data.remove(it)
        }
        locationsManager.onDeleteLocations(selectedItems)
        selectedItems.clear()
    }

    fun switchEditMode() {
        if (isEditMode) {
            locationsManager.onApplyChanges(data)
        }
        isEditMode = !isEditMode
        selectedItems.clear()
        locationsManager.onSwitchEditMode(isEditMode)

        notifyDataSetChanged()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev: LocationDto = data.removeAt(fromPosition)
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
        setupUi(holder, item)
        setupListeners(holder, item)
    }

    private fun setupUi(holder: ViewHolder, item: LocationDto) {
        with(holder.binding) {
            root.tag = item

            if (isEditMode) {
                checkbox.visibility = View.VISIBLE
                checkbox.isChecked = false
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
    private fun setupListeners(holder: ViewHolder, item: LocationDto) {
        with(holder.binding) {
            root.setOnClickListener {
                if (!isEditMode) {
                    locationsManager.onSelectLocation(it.tag as LocationDto)
                } else {
                    checkbox.performClick()
                }
            }

            root.setOnLongClickListener {
                switchEditMode()
                return@setOnLongClickListener true
            }

            dragDropView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    locationsManager.onDragStart(holder)
                }
                return@setOnTouchListener true
            }

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
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
