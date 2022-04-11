package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherInfo
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ManageLocationsViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations: LiveData<MutableList<Location>> get() = _locations
    private val _locations = MutableLiveData<MutableList<Location>>()

    val locationsWeatherInfo: LiveData<MutableSet<ShortWeatherInfo>> get() = _locationsWeatherInfo
    private val _locationsWeatherInfo = MutableLiveData<MutableSet<ShortWeatherInfo>>()

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
                    response.map { ShortWeatherInfo.from(it) }.toMutableSet()
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
            viewModelScope.launch {
                weatherRepository.deleteLocationData(location.id)
                locationRepository.deleteLocation(location)
            }
        }
        _locations.value?.removeAll(locations)
    }

    fun updateLocationPositions(locations: List<Location>) = viewModelScope.launch {
        locations.forEachIndexed { index, location ->
            locationRepository.updatePosition(location.url, index)
        }
        updateLocations()
    }

    class Factory @Inject constructor(
        private val weatherRepository: WeatherRepository,
        private val locationRepository: LocationRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManageLocationsViewModel(weatherRepository, locationRepository) as T
        }
    }
}