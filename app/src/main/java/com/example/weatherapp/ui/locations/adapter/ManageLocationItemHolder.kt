package com.example.weatherapp.ui.locations.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemManageLocationsBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.ui.locations.util.LocationsChangeCallback
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class ManageLocationItemHolder(
    private val binding: ItemManageLocationsBinding,
    private val isEditMode: () -> Boolean,
    private val onSwitchEditMode: (Location) -> Unit,
    private val onAddItem: (Location) -> Unit,
    private val onRemoveItem: (Location) -> Unit,
    private val isItemSelected: (Location) -> Boolean,
) : RecyclerView.ViewHolder(binding.root) {

    fun bindUi(
        item: Location,
        weatherData: ShortWeatherData?,
        tempUnit: TempUnit
    ) = with(binding) {
        root.tag = item

        if (isEditMode()) {
            checkbox.visibility = View.VISIBLE
            checkbox.isChecked = isItemSelected(item)
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


        weatherData?.let {
            val res = itemView.resources

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

    @SuppressLint("ClickableViewAccessibility")
    fun bindListeners(
        item: Location,
        locationsChangeCallback: LocationsChangeCallback
    ) = with(binding) {
        root.setOnClickListener {
            if (isEditMode()) {
                checkbox.performClick()
            } else {
                locationsChangeCallback.onSelectLocation(it.tag as Location)
            }
        }

        root.setOnLongClickListener {
            onSwitchEditMode(item)
            return@setOnLongClickListener true
        }

        dragDropView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                locationsChangeCallback.onDragStart(this@ManageLocationItemHolder)
            }
            return@setOnTouchListener true
        }

        checkbox.setOnClickListener {
            if ((it as CheckBox).isChecked) {
                onAddItem(item)
            } else {
                onRemoveItem(item)
            }
        }
    }
}