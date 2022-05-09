package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.usecase.location.DeleteLocationUseCase
import com.example.weatherapp.domain.usecase.location.GetAllLocationsUseCase
import com.example.weatherapp.domain.usecase.location.UpdateLocationsPositionUseCase
import com.example.weatherapp.domain.usecase.settings.GetTempUnitUseCase
import com.example.weatherapp.domain.usecase.weather.GetShortWeatherDataUseCase
import com.example.weatherapp.domain.utils.WorkResult.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ManageLocationsViewModel(
    private val getTempUnitUseCase: GetTempUnitUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val updateLocationsPositionUseCase: UpdateLocationsPositionUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val getShortWeatherDataUseCase: GetShortWeatherDataUseCase
) : ViewModel() {

    val locations: LiveData<MutableList<Location>> get() = _locations
    private val _locations = MutableLiveData<MutableList<Location>>()

    val locationsShortWeatherData: LiveData<MutableSet<ShortWeatherData>> get() = _locationsShortWeatherData
    private val _locationsShortWeatherData = MutableLiveData<MutableSet<ShortWeatherData>>()

    fun updateData() = viewModelScope.launch(Dispatchers.IO) {
        updateLocations().join()
        updateWeatherInfo()
    }

    private fun updateLocations() = viewModelScope.launch {
        val locations = getAllLocationsUseCase.execute(Unit)
        _locations.postValue(locations.toMutableList())
    }

    fun updateWeatherInfo(locationsToUpdate: List<Location>? = null) = viewModelScope.launch {
        val queryLocations = locationsToUpdate ?: _locations.value
        queryLocations?.let { locations ->
            val resultSet = _locationsShortWeatherData.value ?: mutableSetOf()

            locations.forEach { location ->
                val result = getShortWeatherDataUseCase.execute(location)
                if (result is Success) {
                    resultSet.add(result.data)
                }
            }

            _locationsShortWeatherData.postValue(resultSet)
        }
    }

    fun removeLocations(locations: List<Location>) {
        for (location in locations) {
            viewModelScope.launch { deleteLocationUseCase.execute(location) }
        }
        _locations.value?.removeAll(locations)
    }

    fun updateLocationPositions(locations: List<Location>) = viewModelScope.launch {
        updateLocationsPositionUseCase.execute(locations)
        updateLocations()
    }

    fun getTempUnit(): TempUnit = runBlocking {
        getTempUnitUseCase.execute(Unit)
    }

    class Factory @Inject constructor(
        private val getTempUnitUseCase: GetTempUnitUseCase,
        private val getAllLocationsUseCase: GetAllLocationsUseCase,
        private val updateLocationsPositionUseCase: UpdateLocationsPositionUseCase,
        private val deleteLocationUseCase: DeleteLocationUseCase,
        private val getShortWeatherDataUseCase: GetShortWeatherDataUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManageLocationsViewModel(
                getTempUnitUseCase,
                getAllLocationsUseCase,
                updateLocationsPositionUseCase,
                deleteLocationUseCase,
                getShortWeatherDataUseCase
            ) as T
        }
    }
}