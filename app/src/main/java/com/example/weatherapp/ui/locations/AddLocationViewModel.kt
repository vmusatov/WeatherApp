package com.example.weatherapp.ui.locations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Location
import com.example.weatherapp.notification.WeatherNotificationsBuilder
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.home.HomeViewModel
import javax.inject.Inject

class AddLocationViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val TAG = AddLocationViewModel::class.java.simpleName

    val searchResult: LiveData<List<Location>> get() = _searchResult
    private val _searchResult = MutableLiveData<List<Location>>()

    fun search(q: String) {
        locationRepository.loadSearchAutocomplete(q, {
            _searchResult.postValue(it.map { Location.from(it) })
        }, {
            Log.e(it.message, TAG)
        })
    }

    override fun onCleared() {
        super.onCleared()
        locationRepository.clear()
    }

    class Factory @Inject constructor(
        private val locationRepository: LocationRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AddLocationViewModel(locationRepository) as T
        }
    }
}
