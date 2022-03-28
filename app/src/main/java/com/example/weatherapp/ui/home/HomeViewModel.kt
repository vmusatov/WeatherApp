package com.example.weatherapp.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.model.Astronomy
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.notification.WeatherNotificationsBuilder
import com.example.weatherapp.notification.factory.ExpectPrecipitationsEndFactory
import com.example.weatherapp.notification.factory.ExpectPrecipitationsFactory
import com.example.weatherapp.notification.factory.NoPrecipitationsFactory
import com.example.weatherapp.notification.factory.TempTomorrowFactory
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val TAG = HomeViewModel::class.java.simpleName

    val selectedLocation: LiveData<Location> get() = _selectedLocation
    private val _selectedLocation = MutableLiveData<Location>()

    val weatherData: LiveData<WeatherData> get() = _weatherData
    private val _weatherData = MutableLiveData<WeatherData>()

    val astronomy: LiveData<Astronomy> get() = _astronomy
    private val _astronomy = MutableLiveData<Astronomy>()

    val weatherNotifications: LiveData<List<WeatherNotification>> get() = _weatherNotifications
    private val _weatherNotifications = MutableLiveData<List<WeatherNotification>>()

    val isUpdateInProgress: LiveData<Boolean> get() = _isUpdateInProgress
    private val _isUpdateInProgress = MutableLiveData<Boolean>()

    private val weatherNotificationBuilder = WeatherNotificationsBuilder()

    init {
        weatherNotificationBuilder.addFactory(NoPrecipitationsFactory())
        weatherNotificationBuilder.addFactory(ExpectPrecipitationsFactory())
        weatherNotificationBuilder.addFactory(ExpectPrecipitationsEndFactory())
        weatherNotificationBuilder.addFactory(TempTomorrowFactory())

        updateWeather()
    }

    fun updateWeather(selectedLocation: Location? = null) = viewModelScope.launch {
        val location = selectedLocation ?: locationRepository.getSelectedLocation()
        location?.let {
            _selectedLocation.postValue(it)
            if (!it.isSelected) {
                locationRepository.setLocationIsSelected(it)
            }
            updateForecast(location)
            updateAstronomy(location)
        }
    }

    private fun updateForecast(selectedLocation: Location) {
        _isUpdateInProgress.postValue(true)
        weatherRepository.loadForecast("${selectedLocation.lat}, ${selectedLocation.lon}",
            onSuccess = {
                val data = WeatherData.from(it)
                updateNotifications(data)
                _weatherData.postValue(data)
                _isUpdateInProgress.postValue(false)

                updateLocation(selectedLocation.url, data.location)
            },
            onError = {
                _isUpdateInProgress.postValue(false)
            })
    }

    private fun updateLocation(url: String, location: Location) = viewModelScope.launch {
        locationRepository.setLastUpdatedIsNow(url)
        locationRepository.updateLocalTime(url, location.localtime)
    }

    private fun updateAstronomy(selectedLocation: Location) {
        weatherRepository.loadAstronomy(
            "${selectedLocation.lat}, ${selectedLocation.lon}",
            onSuccess = { _astronomy.postValue(Astronomy.from(it)) },
            onError = {}
        )
    }

    private fun updateNotifications(data: WeatherData) {
        _weatherNotifications.postValue(
            weatherNotificationBuilder.buildNotificationsList(data)
        )
    }

    fun getTempUnit(): TempUnit {
        return weatherRepository.getTempUnit()
    }

    fun saveTempUnit(tempUnit: TempUnit) {
        weatherRepository.saveTempUnit(tempUnit)
    }

    fun parseUvIndex(context: Context, index: Int): String {
        return when (index) {
            in 1..2 -> context.getString(R.string.low)
            in 3..5 -> context.getString(R.string.moderate)
            in 3..5 -> context.getString(R.string.high)
            in 3..5 -> context.getString(R.string.very_high)
            in 11..15 -> context.getString(R.string.extreme)
            else -> context.getString(R.string.undefined)
        }
    }

    fun parseEpaIndex(context: Context, index: Int): String {
        return when (index) {
            1 -> context.getString(R.string.good)
            2 -> context.getString(R.string.moderate)
            3 -> context.getString(R.string.unhealthy_for_sensitive_group)
            4 -> context.getString(R.string.unhealthy)
            5 -> context.getString(R.string.very_unhealthy)
            6 -> context.getString(R.string.hazardous)
            else -> context.getString(R.string.undefined)
        }
    }

    override fun onCleared() {
        super.onCleared()
        weatherRepository.clear()
    }
}