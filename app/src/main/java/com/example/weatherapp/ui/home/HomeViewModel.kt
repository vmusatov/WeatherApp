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
import kotlinx.coroutines.withContext
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

    private var cashedData = mutableListOf<WeatherData>()
    private val weatherNotificationBuilder = WeatherNotificationsBuilder()

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

    init {
        loadCacheData()

        weatherNotificationBuilder.addFactory(NoPrecipitationsFactory())
        weatherNotificationBuilder.addFactory(ExpectPrecipitationsFactory())
        weatherNotificationBuilder.addFactory(ExpectPrecipitationsEndFactory())
        weatherNotificationBuilder.addFactory(TempTomorrowFactory())
    }

    private fun loadCacheData() = viewModelScope.launch {
        cashedData.clear()
        locationRepository.getAllLocations().forEach { location ->
            val dbData = locationRepository.getLocationWithWeather(location.url)
            dbData?.let { cashedData.add(WeatherData.from(it)) }
        }
    }

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
        return withContext(viewModelScope.coroutineContext) {
            val fromDb = location?.let { locationRepository.getLocationByUrl(it.url) }
            val selectedLocation = fromDb ?: locationRepository.getSelectedLocation()

            selectedLocation?.let { locationRepository.setLocationIsSelected(it) }
            _selectedLocation.postValue(selectedLocation)

            selectedLocation
        }
    }

    private suspend fun updateLocationData(location: Location, force: Boolean) {
        val cashed = cashedData.firstOrNull { it.location.url == location.url }
        if (force || needForceUpdate(location)) {
            loadFromApi(location)
        } else if (cashed == null) {
            loadFromDb(location)
        } else {
            loadFromCache(cashed)
        }
    }

    private fun needForceUpdate(location: Location): Boolean {
        if (location.lastUpdated == null) {
            return true
        }

        val datesDiffInMin = DateUtils.datesDiffInMin(location.lastUpdated!!, Date())
        return datesDiffInMin > MAX_MINUTES_WITHOUT_UPDATE
    }

    private fun loadFromCache(data: WeatherData) {
        _weatherData.postValue(data)
        _astronomy.postValue(data.current.astronomy)
        updateNotifications(data)
    }

    private fun addToCache(data: WeatherData) {
        cashedData.removeAll { it.location.url == data.location.url }
        cashedData.add(data)
    }

    private suspend fun loadFromDb(location: Location) {
        val dbData = locationRepository.getLocationWithWeather(location.url)
        if (dbData != null) {
            val data = WeatherData.from(dbData)
            updateNotifications(data)

            _weatherData.postValue(data)
            _astronomy.postValue(data.current.astronomy)
            addToCache(data)
        } else {
            _updateFail.postValue(UpdateFailType.FAIL_LOAD_FROM_DB)
        }
    }

    private fun loadFromApi(location: Location) {
        _isUpdateInProgress.postValue(true)
        weatherRepository.loadForecast(
            q = "${location.lat}, ${location.lon}",
            onSuccess = {
                val data = WeatherData.from(it)

                updateAstronomy(location)
                updateNotifications(data)

                _weatherData.postValue(data)
                _isUpdateInProgress.postValue(false)

                updateDbData(location, data)
                addToCache(data)
            },
            onError = {
                _isUpdateInProgress.postValue(false)
                _updateFail.postValue(UpdateFailType.FAIL_LOAD_FROM_NETWORK)
            })
    }

    private fun updateAstronomy(location: Location) {
        weatherRepository.loadAstronomy(
            q = "${location.lat}, ${location.lon}",
            onSuccess = {
                val astronomy = Astronomy.from(it)

                _astronomy.postValue(astronomy)
                viewModelScope.launch { weatherRepository.updateAstronomy(location.id, astronomy) }
            },
            onError = {}
        )
    }

    private fun updateNotifications(data: WeatherData) {
        _weatherNotifications.postValue(weatherNotificationBuilder.buildNotificationsList(data))
    }

    private fun updateDbData(location: Location, data: WeatherData) = viewModelScope.launch {
        weatherRepository.deleteLocationData(location.id)
        locationRepository.updateLocalTime(location.url, data.location.localtime)

        weatherRepository.addCurrentWeather(location.id, data.current)
        data.daysForecast.forEach { day ->
            val dayId = weatherRepository.addDay(location.id, day)
            day.hours.forEach { weatherRepository.addHour(location.id, dayId.toInt(), it) }
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

    companion object {
        private const val MAX_MINUTES_WITHOUT_UPDATE = 60
    }
}