package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherInfo
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.usecase.location.DeleteLocationUseCase
import com.example.weatherapp.domain.usecase.location.GetAllLocationsUseCase
import com.example.weatherapp.domain.usecase.location.SaveLocationUseCase
import com.example.weatherapp.domain.usecase.location.UpdateLocationsPositionUseCase
import com.example.weatherapp.domain.usecase.settings.GetTempUnitUseCase
import com.example.weatherapp.domain.usecase.weather.GetShortWeatherInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ManageLocationsViewModel(
    private val getTempUnitUseCase: GetTempUnitUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val updateLocationsPositionUseCase: UpdateLocationsPositionUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val getShortWeatherInfoUseCase: GetShortWeatherInfoUseCase
) : ViewModel() {

    val locations: LiveData<MutableList<Location>> get() = _locations
    private val _locations = MutableLiveData<MutableList<Location>>()

    val locationsWeatherInfo: LiveData<MutableSet<ShortWeatherInfo>> get() = _locationsWeatherInfo
    private val _locationsWeatherInfo = MutableLiveData<MutableSet<ShortWeatherInfo>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateData()
        }
    }

    suspend fun updateData() {
        updateLocations().join()
        updateWeatherInfo()
    }

    private fun updateLocations() = viewModelScope.launch {
        val locations = getAllLocationsUseCase.invoke(Unit)
        _locations.postValue(locations.toMutableList())
    }

    fun updateWeatherInfo(locationsToUpdate: List<Location>? = null) = viewModelScope.launch {
        val queryLocations = locationsToUpdate ?: _locations.value
        queryLocations?.let { locations ->
            val result = _locationsWeatherInfo.value ?: mutableSetOf()

            locations.forEach { location ->
                val weatherInfo = getShortWeatherInfoUseCase.invoke(location)
                weatherInfo?.let { result.add(it) }
            }

            _locationsWeatherInfo.postValue(result)
        }
    }

    fun removeLocations(locations: List<Location>) {
        for (location in locations) {
            viewModelScope.launch { deleteLocationUseCase.invoke(location) }
        }
        _locations.value?.removeAll(locations)
    }

    fun updateLocationPositions(locations: List<Location>) = viewModelScope.launch {
        updateLocationsPositionUseCase.invoke(locations)
        updateLocations()
    }

    fun getTempUnit(): TempUnit = runBlocking {
        getTempUnitUseCase.invoke(Unit)
    }

    class Factory @Inject constructor(
        private val getTempUnitUseCase: GetTempUnitUseCase,
        private val getAllLocationsUseCase: GetAllLocationsUseCase,
        private val updateLocationsPositionUseCase: UpdateLocationsPositionUseCase,
        private val deleteLocationUseCase: DeleteLocationUseCase,
        private val getShortWeatherInfoUseCase: GetShortWeatherInfoUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManageLocationsViewModel(
                getTempUnitUseCase,
                getAllLocationsUseCase,
                updateLocationsPositionUseCase,
                deleteLocationUseCase,
                getShortWeatherInfoUseCase
            ) as T
        }
    }
}