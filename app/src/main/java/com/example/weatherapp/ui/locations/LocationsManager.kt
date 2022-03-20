package com.example.weatherapp.ui.locations

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.model.LocationDto

interface LocationsManager {

    fun onSelectLocation(location: LocationDto)

    fun onSwitchEditMode(editMode: Boolean)

    fun onDeleteLocations(locations: List<LocationDto>)

    fun onApplyChanges(locations: List<LocationDto>)

    fun onDragStart(viewHolder: RecyclerView.ViewHolder)
}