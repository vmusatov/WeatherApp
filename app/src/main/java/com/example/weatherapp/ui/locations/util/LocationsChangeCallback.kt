package com.example.weatherapp.ui.locations.util

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.model.Location

interface LocationsChangeCallback {

    fun onSelectLocation(location: Location)

    fun onSwitchEditMode(editMode: Boolean)

    fun onDeleteLocations(locations: List<Location>)

    fun onApplyChanges(locations: List<Location>)

    fun onDragStart(viewHolder: RecyclerView.ViewHolder)
}