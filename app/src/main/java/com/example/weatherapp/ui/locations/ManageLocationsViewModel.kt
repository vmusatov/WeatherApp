package com.example.weatherapp.ui.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.LocationWeatherInfo
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageLocationsViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations: LiveData<MutableList<Location>> get() = _locations
    private val _locations = MutableLiveData<MutableList<Location>>()

    val locationsWeatherInfo: LiveData<MutableSet<LocationWeatherInfo>> get() = _locationsWeatherInfo
    private val _locationsWeatherInfo = MutableLiveData<MutableSet<LocationWeatherInfo>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateLocations().join()
            updateWeatherInfo()
        }
    }

    private fun updateLocations() = viewModelScope.launch {
        val locations = locationRepository.getAllLocations()
        _locations.postValue(locations.toMutableList())
    }

    fun updateWeatherInfo(locations: List<Location>? = null) = viewModelScope.launch {
        val query = locations?.map { it.url } ?: _locations.value?.map { it.url }
        query?.let {
            weatherRepository.loadLocationsCurrentWeather(query)?.let { response ->
                val result = _locationsWeatherInfo.value ?: mutableSetOf()
                result.addAll(
                    response.map { LocationWeatherInfo.from(it) }.toMutableSet()
                )
                _locationsWeatherInfo.postValue(result)
            }
        }
    }

    fun addLocation(location: Location) = viewModelScope.launch {
        locationRepository.addLocation(location)
        updateLocations()
        updateWeatherInfo(listOf(location))
    }

    fun removeLocations(locations: List<Location>) {
        for (location in locations) {
            viewModelScope.launch { locationRepository.removeLocation(location) }
        }
        _locations.value?.removeAll(locations)
    }

    fun updateLocationPositions(locations: List<Location>) = viewModelScope.launch {
        locations.forEachIndexed { index, location ->
            locationRepository.updatePosition(location.url, index)
        }
        updateLocations()
    }
}