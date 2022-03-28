package com.example.weatherapp.ui.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageLocationsViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations: LiveData<List<LocationDto>> get() = _location
    private val _location = MutableLiveData<List<LocationDto>>()

    val locationsWeatherInfo: LiveData<MutableList<LocationWeatherInfo>> get() = _locationsWeatherInfo
    private val _locationsWeatherInfo = MutableLiveData<MutableList<LocationWeatherInfo>>()

    fun updateLocations() = viewModelScope.launch {
        val locations = locationRepository.getAllLocations()
        _location.postValue(locations)
        updateLocationsWeatherInfo(locations)
    }

    private suspend fun updateLocationsWeatherInfo(locations: List<LocationDto>) {
        val query = locations.map { it.url }
        weatherRepository.loadLocationsCurrentWeather(query)?.let { response ->
            val locationsInfo = response.map {
                LocationWeatherInfo(
                    locationName = it.location.name,
                    tempC = it.current.tempC,
                    tempF = it.current.tempF,
                    conditionIconUrl = it.current.condition.icon
                )
            }
            _locationsWeatherInfo.postValue(locationsInfo.toMutableList())
        }
    }

    fun addLocation(searchLocation: LocationDto) = viewModelScope.launch {
        searchLocation.position = locationRepository.getLocationsCount()
        locationRepository.addLocation(searchLocation)
        updateLocations()
    }

    fun removeLocation(location: LocationDto) = viewModelScope.launch {
        locationRepository.removeLocation(location)
    }

    fun updateLocationPosition(location: LocationDto, position: Int) = viewModelScope.launch {
        locationRepository.updatePosition(location.url, position)
        updateLocations()
    }
}