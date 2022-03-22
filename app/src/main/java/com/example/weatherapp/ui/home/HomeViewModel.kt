package com.example.weatherapp.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.model.Hour
import com.example.weatherapp.data.remote.model.LocationWeatherForecast
import com.example.weatherapp.model.AstronomyDto
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.notification.WeatherNotificationsBuilder
import com.example.weatherapp.notification.factory.ExpectPrecipitationsEndFactory
import com.example.weatherapp.notification.factory.ExpectPrecipitationsFactory
import com.example.weatherapp.notification.factory.NoPrecipitationsFactory
import com.example.weatherapp.notification.factory.TempTomorrowFactory
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.locations.LocationWeatherInfo
import com.example.weatherapp.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val TAG = HomeViewModel::class.java.simpleName

    val weatherForecast: LiveData<LocationWeatherForecast> get() = _weatherForecast
    private val _weatherForecast = MutableLiveData<LocationWeatherForecast>()

    val hourlyForecast: LiveData<List<Hour>> get() = _hourlyForecast
    private val _hourlyForecast = MutableLiveData<List<Hour>>()

    val locationsWeatherCurrent: LiveData<MutableList<LocationWeatherInfo>> get() = _locationsWeatherCurrent
    private val _locationsWeatherCurrent = MutableLiveData<MutableList<LocationWeatherInfo>>()

    val astronomy: LiveData<AstronomyDto> get() = _astronomy
    private val _astronomy = MutableLiveData<AstronomyDto>()

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

        updateAll()
    }

    fun updateAll() {
        updateForecast()
        updateAstronomy()
        updateLocationsWeatherInfo()
    }

    fun updateForecast() {
        val location = getSelectedLocation()
        location?.let {
            _isUpdateInProgress.value = true
            weatherRepository.loadForecast("${location.lat}, ${location.lon}",
                onSuccess = {
                    _weatherForecast.postValue(it)
                    updateHourlyForecast(it)
                    updateNotifications(it)
                    _isUpdateInProgress.postValue(false)
                },
                onError = {
                    _isUpdateInProgress.postValue(false)
                })
        }
    }

    fun updateLocationsWeatherInfo() {
        viewModelScope.launch {
            val query = getLocations().map { it.url }
            weatherRepository.loadLocationsCurrentWeather(query)?.let { response ->
                val locationsInfo = response.map {
                    LocationWeatherInfo(
                        locationName = it.location.name,
                        tempC = it.current.tempC,
                        tempF = it.current.tempF,
                        conditionIconUrl = it.current.condition.icon
                    )
                }
                _locationsWeatherCurrent.postValue(locationsInfo.toMutableList())
            }
        }
    }

    fun updateAstronomy() {
        val location = getSelectedLocation()
        location?.let {
            weatherRepository.loadAstronomy("${location.lat}, ${location.lon}",
                onSuccess = {
                    _astronomy.value = AstronomyDto(
                        it.location.name,
                        it.astronomy.astro.sunrise,
                        it.astronomy.astro.sunset
                    )
                },
                onError = {}
            )
        }
    }

    private fun updateNotifications(forecast: LocationWeatherForecast) {
        viewModelScope.launch(Dispatchers.IO) {
            val notifications = weatherNotificationBuilder.buildNotificationsList(forecast)
            _weatherNotifications.postValue(notifications)
        }
    }

    private fun updateHourlyForecast(weatherForecast: LocationWeatherForecast) {
        viewModelScope.launch(Dispatchers.IO) {
            val hours = mutableListOf<Hour>()
            val forecastDays = weatherForecast.forecast.forecastDays

            if (forecastDays.isNotEmpty()) {
                val nowHourAsInt = DateUtils.getHourFromDate(weatherForecast.location.localtime)
                val todayHours = forecastDays.first().hours

                if (todayHours.size > nowHourAsInt) {
                    hours.addAll(todayHours.subList(nowHourAsInt, todayHours.size))
                }

                if (forecastDays.size > 1 && forecastDays[1].hours.size > nowHourAsInt) {
                    val tomorrowHours = forecastDays[1].hours
                    hours.addAll(tomorrowHours.subList(0, nowHourAsInt))
                }
            }

            _hourlyForecast.postValue(hours)
        }
    }

    fun getTempUnit(): TempUnit {
        return weatherRepository.getTempUnit()
    }

    fun saveTempUnit(tempUnit: TempUnit) {
        weatherRepository.saveTempUnit(tempUnit)
    }

    fun getSelectedLocation(): LocationDto? {
        return locationRepository.getSelectedLocation()
    }

    private fun getLocations(): List<LocationDto> {
        return locationRepository.getLocations()
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