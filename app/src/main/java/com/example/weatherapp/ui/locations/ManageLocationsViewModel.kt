package com.example.weatherapp.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.di.DefaultDispatcher
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.usecase.location.*
import com.example.weatherapp.domain.usecase.settings.ListenTempUnitUseCase
import com.example.weatherapp.domain.usecase.weather.GetShortWeatherDataUseCase
import com.example.weatherapp.domain.utils.WorkResult.Success
import com.example.weatherapp.ui.utils.ViewModelWithUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageScreenUiState(
    val locations: MutableList<Location> = mutableListOf(),
    val weatherData: MutableSet<ShortWeatherData> = mutableSetOf(),
    val tempUnit: TempUnit = TempUnit.DEFAULT
)

class ManageLocationsViewModel(
    private val listenTempUnitUseCase: ListenTempUnitUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val updateLocationsPositionUseCase: UpdateLocationsPositionUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val getShortWeatherDataUseCase: GetShortWeatherDataUseCase,
    private val setLocationIsSelectedUseCase: SetLocationIsSelectedUseCase,
    private val listenAddLocationUseCase: ListenAddLocationUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModelWithUiState<ManageScreenUiState>() {

    override fun createDefaultState(): ManageScreenUiState = ManageScreenUiState()

    init {
        updateData()
        setupListeners()
    }

    private fun setupListeners() {
        viewModelScope.launch {
            listenAddLocationUseCase.execute(Unit).collect { updateData() }
        }
        viewModelScope.launch {
            listenTempUnitUseCase.execute(Unit).collect { newTempUnit ->
                updateUiState { it.copy(tempUnit = newTempUnit) }
            }
        }
    }

    private fun updateData() = viewModelScope.launch(dispatcher) {
        updateLocations().join()
        updateWeatherInfo()
    }

    private fun updateLocations() = viewModelScope.launch {
        val locations = getAllLocationsUseCase.execute(Unit)
        updateUiState { it.copy(locations = locations.toMutableList()) }
    }

    private fun updateWeatherInfo(locationsToUpdate: List<Location>? = null) =
        viewModelScope.launch {
            val queryLocations = locationsToUpdate ?: uiStateValue?.locations
            queryLocations?.let { locations ->
                val resultSet = uiStateValue?.weatherData ?: mutableSetOf()

                locations.forEach { location ->
                    val result = getShortWeatherDataUseCase.execute(location)
                    if (result is Success) {
                        resultSet.add(result.data)
                    }
                }

                updateUiState { it.copy(weatherData = resultSet) }
            }
        }

    fun removeLocations(locations: List<Location>) {
        for (location in locations) {
            viewModelScope.launch { deleteLocationUseCase.execute(location) }
        }

        uiStateValue?.locations?.removeAll(locations)
    }

    fun updateLocationPositions(locations: List<Location>) = viewModelScope.launch {
        updateLocationsPositionUseCase.execute(locations)
        updateLocations()
    }

    fun setLocationIsSelected(location: Location) = viewModelScope.launch {
        setLocationIsSelectedUseCase.execute(location)
        updateWeatherInfo(listOf(location))
    }

    class Factory @Inject constructor(
        private val listenTempUnitUseCase: ListenTempUnitUseCase,
        private val getAllLocationsUseCase: GetAllLocationsUseCase,
        private val updateLocationsPositionUseCase: UpdateLocationsPositionUseCase,
        private val deleteLocationUseCase: DeleteLocationUseCase,
        private val getShortWeatherDataUseCase: GetShortWeatherDataUseCase,
        private val setLocationIsSelectedUseCase: SetLocationIsSelectedUseCase,
        private val listenAddLocationUseCase: ListenAddLocationUseCase,
        @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManageLocationsViewModel(
                listenTempUnitUseCase,
                getAllLocationsUseCase,
                updateLocationsPositionUseCase,
                deleteLocationUseCase,
                getShortWeatherDataUseCase,
                setLocationIsSelectedUseCase,
                listenAddLocationUseCase,
                ioDispatcher
            ) as T
        }
    }
}