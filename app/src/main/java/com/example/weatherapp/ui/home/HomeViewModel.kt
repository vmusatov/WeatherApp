package com.example.weatherapp.ui.home

import android.content.Context
import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.model.WeatherData
import com.example.weatherapp.domain.model.WeatherNotification
import com.example.weatherapp.domain.usecase.location.GetSelectedLocationUseCase
import com.example.weatherapp.domain.usecase.location.SetLocationIsSelectedUseCase
import com.example.weatherapp.domain.usecase.notification.CreateNotificationsListUseCase
import com.example.weatherapp.domain.usecase.settings.GetTempUnitUseCase
import com.example.weatherapp.domain.usecase.weather.Data
import com.example.weatherapp.domain.usecase.weather.GetWeatherDataUseCase
import com.example.weatherapp.domain.usecase.weather.ParseEpaIndexUseCase
import com.example.weatherapp.domain.usecase.weather.ParseUvIndexUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

enum class UpdateFailType {
    NO_LOCATION,
    FAIL_LOAD_FROM_DB,
    FAIL_LOAD_FROM_NETWORK
}

class HomeViewModel(
    private val getTempUnitUseCase: GetTempUnitUseCase,
    private val createNotificationsListUseCase: CreateNotificationsListUseCase,
    private val getSelectedLocationUseCase: GetSelectedLocationUseCase,
    private val setLocationIsSelectedUseCase: SetLocationIsSelectedUseCase,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val parseEpaIndexUseCase: ParseEpaIndexUseCase,
    private val parseUvIndexUseCase: ParseUvIndexUseCase
) : ViewModel() {

    val selectedLocation: LiveData<Location?> get() = _selectedLocation
    private val _selectedLocation = MutableLiveData<Location?>()

    val weatherData: LiveData<WeatherData> get() = _weatherData
    private val _weatherData = MutableLiveData<WeatherData>()

    val weatherNotifications: LiveData<List<WeatherNotification>> get() = _weatherNotifications
    private val _weatherNotifications = MutableLiveData<List<WeatherNotification>>()

    val isUpdateInProgress: LiveData<Boolean> get() = _isUpdateInProgress
    private val _isUpdateInProgress = MutableLiveData<Boolean>()

    val updateFail: LiveData<UpdateFailType?> get() = _updateFail
    private val _updateFail = MutableLiveData<UpdateFailType?>()

    fun updateWeather(location: Location? = null, force: Boolean = false) = viewModelScope.launch {
        _updateFail.postValue(null)
        val selectedLocation = selectLocation(location)

        if (selectedLocation != null) {
            updateLocationData(selectedLocation, force)
        } else {
            _updateFail.postValue(UpdateFailType.NO_LOCATION)
        }
    }

    private suspend fun selectLocation(location: Location?): Location? {
        val selectedLocation = location ?: getSelectedLocationUseCase.invoke(Unit)

        selectedLocation?.let { setLocationIsSelectedUseCase.invoke(it) }
        _selectedLocation.postValue(selectedLocation)

        return selectedLocation
    }

    private suspend fun updateLocationData(location: Location, force: Boolean) {
        _isUpdateInProgress.postValue(true)

        val requestData = Data(location = location, forceLoad = force)
        val weatherData = getWeatherDataUseCase.invoke(requestData)

        if (weatherData != null) {
            updateNotifications(weatherData)
            _weatherData.postValue(weatherData)
        } else {
            _updateFail.postValue(UpdateFailType.FAIL_LOAD_FROM_NETWORK)
        }
        _isUpdateInProgress.postValue(false)
    }

    private fun updateNotifications(data: WeatherData) = viewModelScope.launch {
        _weatherNotifications.postValue(createNotificationsListUseCase.invoke(data))
    }

    fun getTempUnit(): TempUnit = runBlocking {
        getTempUnitUseCase.invoke(Unit)
    }

    fun parseUvIndex(context: Context, index: Int): String = runBlocking {
        context.getString(parseUvIndexUseCase.invoke(index))
    }

    fun parseEpaIndex(context: Context, index: Int): String = runBlocking {
        context.getString(parseEpaIndexUseCase.invoke(index))
    }

    class Factory @Inject constructor(
        private val getTempUnitUseCase: GetTempUnitUseCase,
        private val createNotificationsListUseCase: CreateNotificationsListUseCase,
        private val getSelectedLocationUseCase: GetSelectedLocationUseCase,
        private val setLocationIsSelectedUseCase: SetLocationIsSelectedUseCase,
        private val getWeatherDataUseCase: GetWeatherDataUseCase,
        private val parseEpaIndexUseCase: ParseEpaIndexUseCase,
        private val parseUvIndexUseCase: ParseUvIndexUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(
                getTempUnitUseCase,
                createNotificationsListUseCase,
                getSelectedLocationUseCase,
                setLocationIsSelectedUseCase,
                getWeatherDataUseCase,
                parseEpaIndexUseCase,
                parseUvIndexUseCase
            ) as T
        }
    }
}