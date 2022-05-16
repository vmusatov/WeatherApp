package com.example.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import com.example.weatherapp.domain.usecase.location.GetSelectedLocationUseCase
import com.example.weatherapp.domain.usecase.location.ListenSelectedLocationUseCase
import com.example.weatherapp.domain.usecase.notification.CreateNotificationsListUseCase
import com.example.weatherapp.domain.usecase.settings.GetTempUnitUseCase
import com.example.weatherapp.domain.usecase.settings.ListenTempUnitUseCase
import com.example.weatherapp.domain.usecase.weather.GetWeatherDataUseCase
import com.example.weatherapp.domain.usecase.weather.GetWeatherDataUseCase.Data
import com.example.weatherapp.domain.utils.WorkResult.Fail
import com.example.weatherapp.domain.utils.WorkResult.Success
import com.example.weatherapp.exception.DbException
import com.example.weatherapp.exception.NetworkException
import com.example.weatherapp.ui.utils.LoadErrorType
import com.example.weatherapp.ui.utils.UiState
import com.example.weatherapp.ui.utils.ViewModelWithUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeScreenUiState(
    val tempUnit: TempUnit = TempUnit.DEFAULT,
    val weatherData: WeatherData? = null,
    val weatherNotifications: List<WeatherNotification> = emptyList(),
    val uiState: UiState = UiState.NOTHING,
    val loadErrorType: LoadErrorType? = null
)

class HomeViewModel(
    private val getTempUnitUseCase: GetTempUnitUseCase,
    private val listenTempUnitUseCase: ListenTempUnitUseCase,
    private val listenSelectedLocationUseCase: ListenSelectedLocationUseCase,
    private val createNotificationsListUseCase: CreateNotificationsListUseCase,
    private val getSelectedLocationUseCase: GetSelectedLocationUseCase,
    private val getWeatherDataUseCase: GetWeatherDataUseCase
) : ViewModelWithUiState<HomeScreenUiState>() {

    override fun createDefaultState(): HomeScreenUiState = HomeScreenUiState()

    init {
        setupListeners()
        updateWeather()
    }

    private fun setupListeners() {
        viewModelScope.launch {
            listenTempUnitUseCase.execute(Unit).collect { newTempUnit ->
                updateUiState { it.copy(tempUnit = newTempUnit) }
            }
        }
        viewModelScope.launch {
            listenSelectedLocationUseCase.execute(Unit).collect { updateWeather(it) }
        }
    }

    fun updateWeather(location: Location? = null, force: Boolean = false) = viewModelScope.launch {
        val selectedLocation = location ?: getSelectedLocationUseCase.execute(Unit)

        if (selectedLocation != null) {
            updateLocationWeather(selectedLocation, force)
        } else {
            updateUiState {
                it.copy(
                    uiState = UiState.LOAD_ERROR,
                    loadErrorType = LoadErrorType.NO_LOCATION
                )
            }
        }
    }

    private suspend fun updateLocationWeather(location: Location, force: Boolean) {
        updateUiState { it.copy(uiState = UiState.LOADING) }

        val requestData = Data(location = location, forceLoad = force)
        when (val result = getWeatherDataUseCase.execute(requestData)) {
            is Success -> handleSuccessResult(result.data)
            is Fail -> handleFailResult(result)
        }
    }

    private suspend fun handleSuccessResult(weatherData: WeatherData) {
        val tempUnit = getTempUnitUseCase.execute(Unit)
        val notifications = createNotificationsListUseCase.execute(weatherData)

        updateUiState {
            it.copy(
                uiState = UiState.READY_TO_SHOW,
                tempUnit = tempUnit,
                weatherData = weatherData,
                weatherNotifications = notifications
            )
        }
    }

    private fun handleFailResult(result: Fail<Any>) {
        updateUiState {
            when (result.exception) {
                is NetworkException -> {
                    it.copy(
                        uiState = UiState.LOAD_ERROR,
                        loadErrorType = LoadErrorType.FAIL_LOAD_FROM_NETWORK
                    )
                }
                is DbException -> {
                    it.copy(
                        uiState = UiState.LOAD_ERROR,
                        loadErrorType = LoadErrorType.FAIL_LOAD_FROM_DB
                    )
                }
                else -> {
                    it.copy(
                        uiState = UiState.LOAD_ERROR,
                        loadErrorType = LoadErrorType.UNDEFINED
                    )
                }
            }
        }
    }

    class Factory @Inject constructor(
        private val getTempUnitUseCase: GetTempUnitUseCase,
        private val listenTempUnitUseCase: ListenTempUnitUseCase,
        private val listenSelectedLocationUseCase: ListenSelectedLocationUseCase,
        private val createNotificationsListUseCase: CreateNotificationsListUseCase,
        private val getSelectedLocationUseCase: GetSelectedLocationUseCase,
        private val getWeatherDataUseCase: GetWeatherDataUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                getTempUnitUseCase,
                listenTempUnitUseCase,
                listenSelectedLocationUseCase,
                createNotificationsListUseCase,
                getSelectedLocationUseCase,
                getWeatherDataUseCase
            ) as T
        }
    }
}