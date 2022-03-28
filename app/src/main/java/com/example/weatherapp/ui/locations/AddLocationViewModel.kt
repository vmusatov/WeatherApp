package com.example.weatherapp.ui.locations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.Location
import com.example.weatherapp.repository.LocationRepository

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
}