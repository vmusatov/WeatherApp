package com.example.weatherapp.ui.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.LocationWeatherInfo
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class ManageLocationsViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations: LiveData<List<Location>> get() = _location
    private val _location = MutableLiveData<List<Location>>()

    val locationsWeatherInfo: LiveData<MutableList<LocationWeatherInfo>> get() = _locationsWeatherInfo
    private val _locationsWeatherInfo = MutableLiveData<MutableList<LocationWeatherInfo>>()

    fun updateLocations() = viewModelScope.launch {
        val locations = locationRepository.getAllLocations()
        _location.postValue(locations)
        updateLocationsWeatherInfo(locations)
    }

    private suspend fun updateLocationsWeatherInfo(locations: List<Location>) {
        val query = locations.map { it.url }
        weatherRepository.loadLocationsCurrentWeather(query)?.let { response ->
            _locationsWeatherInfo.postValue(
                response.map { LocationWeatherInfo.from(it) }.toMutableList()
            )
        }
    }

    fun addLocation(location: Location) = viewModelScope.launch {
        location.position = locationRepository.getLocationsCount()
        locationRepository.addLocation(location)
        updateLocations()
    }

    fun removeLocation(location: Location) = viewModelScope.launch {
        locationRepository.removeLocation(location)
    }

    fun updateLocationPosition(location: Location, position: Int) = viewModelScope.launch {
        locationRepository.updatePosition(location.url, position)
        updateLocations()
    }
}