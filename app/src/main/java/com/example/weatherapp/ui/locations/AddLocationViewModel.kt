package com.example.weatherapp.ui.locations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.repository.LocationRepository

class AddLocationViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val TAG = AddLocationViewModel::class.java.simpleName

    val searchResult: LiveData<List<LocationDto>> get() = _searchResult
    private val _searchResult = MutableLiveData<List<LocationDto>>()

    fun search(q: String) {
        locationRepository.loadSearchAutocomplete(q, {
            _searchResult.postValue(it.map { LocationDto.fromSearchLocation(it) })
        }, {
            Log.e(it.message, TAG)
        })
    }

    override fun onCleared() {
        super.onCleared()
        locationRepository.clear()
    }
}