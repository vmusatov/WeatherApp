package com.example.weatherapp.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.model.Hour
import com.example.weatherapp.data.remote.model.WeatherForecast
import com.example.weatherapp.model.AstronomyDto
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.repository.AstronomyRepository
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.locations.LocationInfo
import com.example.weatherapp.util.DateUtils

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val astronomyRepository: AstronomyRepository
) : ViewModel() {

    private val TAG = HomeViewModel::class.java.simpleName

    val weatherForecast: LiveData<WeatherForecast> get() = _weatherForecast
    private val _weatherForecast = MutableLiveData<WeatherForecast>()

    val hourlyForecast: LiveData<List<Hour>> get() = _hourlyForecast
    private val _hourlyForecast = MutableLiveData<List<Hour>>()

    val locationsWeatherInfo: LiveData<MutableSet<LocationInfo>> get() = _locationsWeatherInfo
    private val _locationsWeatherInfo = MutableLiveData<MutableSet<LocationInfo>>()

    val astronomy: LiveData<AstronomyDto> get() = _astronomy
    private val _astronomy = MutableLiveData<AstronomyDto>()

    val isUpdateInProgress: LiveData<Boolean> get() = _isUpdateInProgress
    private val _isUpdateInProgress = MutableLiveData<Boolean>()

    init {
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
                    _hourlyForecast.postValue(parseHourlyForecast(it))
                    _isUpdateInProgress.postValue(false)
                },
                onError = {
                    _isUpdateInProgress.postValue(false)
                    Log.e(it.message, TAG)
                })
        }
    }

    fun updateLocationsWeatherInfo() {
        _locationsWeatherInfo.value = mutableSetOf()
        weatherRepository.loadForecasts(
            getLocations().map { it.name },
            onSuccess = {
                val locationInfo = LocationInfo(
                    locationName = it.location.name,
                    tempC = it.current.tempC,
                    tempF = it.current.tempF,
                    conditionIconUrl = it.current.condition.icon
                )
                _locationsWeatherInfo.value?.add(locationInfo)
            },
            onError = {
                _isUpdateInProgress.postValue(false)
                Log.e(it.message, TAG)
            }
        )
    }

    fun updateAstronomy() {
        val location = getSelectedLocation()
        location?.let {
            _isUpdateInProgress.value = true
            astronomyRepository.loadAstronomy("${location.lat}, ${location.lon}",
                onSuccess = {
                    _isUpdateInProgress.postValue(false)
                    _astronomy.value = AstronomyDto(
                        it.location.name,
                        it.astronomy.astro.sunrise,
                        it.astronomy.astro.sunset
                    )
                },
                onError = {
                    _isUpdateInProgress.postValue(false)
                    Log.e(it.message, TAG)
                })
        }
    }

    private fun parseHourlyForecast(weatherForecast: WeatherForecast): List<Hour> {
        val byHour = mutableListOf<Hour>()
        val forecastDays = weatherForecast.forecast.forecastDay

        if (forecastDays.isNotEmpty()) {
            val nowHour = DateUtils.getHourFromDate(weatherForecast.location.localtime)
            val todayByHour = forecastDays.first().hour

            if (todayByHour.size > nowHour) {
                byHour.addAll(todayByHour.subList(nowHour, todayByHour.size))
            }

            if (forecastDays.size > 1 && forecastDays[1].hour.size > nowHour) {
                val tomorrowByHour = forecastDays[1].hour
                byHour.addAll(tomorrowByHour.subList(0, nowHour))
            }
        }

        return byHour
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
        weatherRepository.dispose()
        astronomyRepository.dispose()
    }
}