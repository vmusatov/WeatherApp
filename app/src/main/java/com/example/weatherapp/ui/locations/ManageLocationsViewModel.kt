package com.example.weatherapp.ui.locations

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.repository.LocationRepository

class ManageLocationsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    fun getLocations(): List<LocationDto> {
        return locationRepository.getLocations()
    }

    fun removeLocation(location: LocationDto) {
        locationRepository.removeLocation(location)
    }

    fun setLocationIsSelected(location: LocationDto) {
        locationRepository.setLocationIsSelected(location)
    }

    fun updateLocationPosition(location: LocationDto, position: Int) {
        locationRepository.updatePosition(location.url, position)
    }
}