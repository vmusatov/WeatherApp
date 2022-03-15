package com.example.weatherapp.ui.locations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.remote.SearchApi
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.repository.LocationRepository
import com.example.weatherapp.ui.home.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddLocationViewModel(
    private val searchApi: SearchApi,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val TAG = AddLocationViewModel::class.java.simpleName

    private val disposeBag = CompositeDisposable()

    val searchResult: LiveData<List<LocationDto>> get() = _searchResult
    private val _searchResult = MutableLiveData<List<LocationDto>>()

    fun search(q: String) {
        val result = searchApi.getSearchResult(q)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _searchResult.postValue(it.map { LocationDto.fromSearchLocation(it) })
            }, {
                Log.e(it.message, TAG)
            })

        disposeBag.add(result)
    }

    fun saveLocation(searchLocation: LocationDto) {
        locationRepository.addLocation(searchLocation)
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.dispose()
    }
}