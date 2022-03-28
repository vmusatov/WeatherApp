package com.example.weatherapp.ui.locations

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.model.Location

interface LocationsManager {

    fun onSelectLocation(location: Location)

    fun onSwitchEditMode(editMode: Boolean)

    fun onDeleteLocations(locations: List<Location>)

    fun onApplyChanges(locations: List<Location>)

    fun onDragStart(viewHolder: RecyclerView.ViewHolder)
}