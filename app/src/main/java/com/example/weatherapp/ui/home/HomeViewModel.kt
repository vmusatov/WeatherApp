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
import com.example.weatherapp.util.DateUtils
import kotlinx.coroutines.launch
import java.util.*

enum class UpdateFailType {
    NO_LOCATION,
    FAIL_LOAD_FROM_DB,
    FAIL_LOAD_FROM_NETWORK
}

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val TAG = HomeViewModel::class.java.simpleName

    val selectedLocation: LiveData<Location?> get() = _selectedLocation
    private val _selectedLocation = MutableLiveData<Location?>()

    val weatherData: LiveData<WeatherData> get() = _weatherData
    private val _weatherData = MutableLiveData<WeatherData>()

    val astronomy: LiveData<Astronomy> get() = _astronomy
    private val _astronomy = MutableLiveData<Astronomy>()

    val weatherNotifications: LiveData<List<WeatherNotification>> get() = _weatherNotifications
    private val _weatherNotifications = MutableLiveData<List<WeatherNotification>>()

    val isUpdateInProgress: LiveData<Boolean> get() = _isUpdateInProgress
    private val _isUpdateInProgress = MutableLiveData<Boolean>()

    val updateFail: LiveData<UpdateFailType?> get() = _updateFail
    private val _updateFail = MutableLiveData<UpdateFailType?>()

    private val weatherNotificationBuilder = WeatherNotificationsBuilder()

    init {
        weatherNotificationBuilder.addFactory(NoPrecipitationsFactory())
        weatherNotificationBuilder.addFactory(ExpectPrecipitationsFactory())
        weatherNotificationBuilder.addFactory(ExpectPrecipitationsEndFactory())
        weatherNotificationBuilder.addFactory(TempTomorrowFactory())
    }

    fun updateWeather(location: Location? = null, force: Boolean = false) = viewModelScope.launch {
        _updateFail.postValue(null)

        val selectedLocation = location ?: locationRepository.getSelectedLocation()
        _selectedLocation.postValue(selectedLocation)

        if (selectedLocation != null) {
            if (!selectedLocation.isSelected) {
                locationRepository.setLocationIsSelected(selectedLocation)
            }

            _isUpdateInProgress.postValue(true)

            if (force || needForceUpdate(selectedLocation)) {
                loadFromApi(selectedLocation)
            } else {
                loadFromDb(selectedLocation)
            }
        } else {
            _updateFail.postValue(UpdateFailType.NO_LOCATION)
        }
    }

    private fun needForceUpdate(location: Location): Boolean {
        if (location.lastUpdated == null) {
            return true
        }

        val datesDiffInMin = DateUtils.datesDiffInMin(location.lastUpdated!!, Date())
        return datesDiffInMin > 60
    }

    private suspend fun loadFromDb(location: Location) {
        val dbData = locationRepository.getLocationWithWeather(location.url)
        if (dbData != null) {
            val weatherData = WeatherData.from(dbData)
            updateNotifications(weatherData)

            _weatherData.postValue(weatherData)
            _astronomy.postValue(weatherData.current.astronomy)
        } else {
            _updateFail.postValue(UpdateFailType.FAIL_LOAD_FROM_DB)
        }
        _isUpdateInProgress.postValue(false)
    }

    private fun loadFromApi(location: Location) {
        weatherRepository.loadForecast("${location.lat}, ${location.lon}",
            onSuccess = {
                val data = WeatherData.from(it)

                updateAstronomy(location)
                updateNotifications(data)
                _weatherData.postValue(data)
                _isUpdateInProgress.postValue(false)

                updateDbData(location, data)
            },
            onError = {
                _isUpdateInProgress.postValue(false)
                _updateFail.postValue(UpdateFailType.FAIL_LOAD_FROM_NETWORK)
            })
    }

    private fun updateAstronomy(selectedLocation: Location) {
        weatherRepository.loadAstronomy(
            "${selectedLocation.lat}, ${selectedLocation.lon}",
            onSuccess = {
                val astronomy = Astronomy.from(it)

                _astronomy.postValue(astronomy)
                viewModelScope.launch {
                    weatherRepository.updateAstronomy(selectedLocation.id, astronomy)
                }
            },
            onError = {}
        )
    }

    private fun updateNotifications(data: WeatherData) {
        _weatherNotifications.postValue(
            weatherNotificationBuilder.buildNotificationsList(data)
        )
    }

    private fun updateDbData(location: Location, data: WeatherData) = viewModelScope.launch {
        weatherRepository.deleteLocationData(location.id)
        locationRepository.updateLocalTime(location.url, data.location.localtime)

        weatherRepository.addCurrentWeather(location.id, data.current)
        data.daysForecast.forEach { day ->
            val dayId = weatherRepository.addDay(location.id, day)

            day.hours.forEach {
                weatherRepository.addHour(location.id, dayId.toInt(), it)
            }
        }

        locationRepository.setLastUpdatedIsNow(location.url)
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
        locationRepository.clear()
    }
}