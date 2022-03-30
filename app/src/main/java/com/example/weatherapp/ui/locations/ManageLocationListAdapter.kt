package com.example.weatherapp.ui.locations

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemManageLocationsBinding
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.LocationWeatherInfo
import com.example.weatherapp.model.TempUnit
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

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
        dataInfo = info.toMutableList()
        notifyDataSetChanged()
    }

    fun update(data: List<Location>) {
        this.data = data.sortedBy { it.position }.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteSelected() {
        selectedItems.forEach {
            notifyItemRemoved(data.indexOf(it))
            data.remove(it)
        }
        locationsChangeCallback.onDeleteLocations(selectedItems)
        selectedItems.clear()
    }

    fun switchEditMode() {
        if (isEditMode) {
            locationsChangeCallback.onApplyChanges(data)
        }
        isEditMode = !isEditMode
        selectedItems.clear()
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
        setupUi(holder, item)
        setupListeners(holder, item)
    }

    private fun setupUi(holder: ViewHolder, item: Location) {
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
    private fun setupListeners(holder: ViewHolder, item: Location) {
        with(holder.binding) {
            root.setOnClickListener {
                if (!isEditMode) {
                    locationsChangeCallback.onSelectLocation(it.tag as Location)
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
                    locationsChangeCallback.onDragStart(holder)
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
