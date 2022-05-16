package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.usecase.location.GetAllLocationsUseCase
import com.example.weatherapp.domain.usecase.location.GetLocationsByNameUseCase
import com.example.weatherapp.domain.usecase.location.SaveLocationUseCase
import com.example.weatherapp.domain.utils.WorkResult.Fail
import com.example.weatherapp.domain.utils.WorkResult.Success
import com.example.weatherapp.exception.NetworkException
import com.example.weatherapp.ui.utils.LoadErrorType
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddLocationViewModel(
    private val getLocationsByNameUseCase: GetLocationsByNameUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase
) : ViewModel() {

    val searchResult: LiveData<List<Location>> get() = _searchResult
    private val _searchResult = MutableLiveData<List<Location>>()

    val loadErrorType: LiveData<LoadErrorType?> get() = _updateFail
    private val _updateFail = MutableLiveData<LoadErrorType?>()

    private val savedLocations = mutableListOf<Location>()

    init {
        viewModelScope.launch {
            savedLocations.addAll(getAllLocationsUseCase.execute(Unit))
        }
    }

    fun search(q: String) = viewModelScope.launch {
        when (val result = getLocationsByNameUseCase.execute(q)) {
            is Success -> _searchResult.postValue(result.data)
            is Fail -> handleFailResult(result)
        }
    }

    fun saveLocation(location: Location) = viewModelScope.launch {
        saveLocationUseCase.execute(location)
        savedLocations.add(location)
    }

    fun isLocationExist(location: Location): Boolean = savedLocations.any { it.url == location.url }

    private fun handleFailResult(result: Fail<Any>) {
        when (result.exception) {
            is NetworkException -> _updateFail.postValue(LoadErrorType.FAIL_LOAD_FROM_NETWORK)
            else -> _updateFail.postValue(LoadErrorType.UNDEFINED)
        }
    }

    class Factory @Inject constructor(
        private val getLocationsByNameUseCase: GetLocationsByNameUseCase,
        private val saveLocationUseCase: SaveLocationUseCase,
        private val getAllLocationsUseCase: GetAllLocationsUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddLocationViewModel(
                getLocationsByNameUseCase,
                saveLocationUseCase,
                getAllLocationsUseCase
            ) as T
        }
    }
}
