package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.exception.NetworkException
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.usecase.location.GetAllLocationsUseCase
import com.example.weatherapp.domain.usecase.location.GetLocationsByNameUseCase
import com.example.weatherapp.domain.usecase.location.SaveLocationUseCase
import com.example.weatherapp.domain.utils.WorkResult.Fail
import com.example.weatherapp.domain.utils.WorkResult.Success
import com.example.weatherapp.ui.UpdateFailType
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AddLocationViewModel(
    private val getLocationsByNameUseCase: GetLocationsByNameUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase
) : ViewModel() {

    val searchResult: LiveData<List<Location>> get() = _searchResult
    private val _searchResult = MutableLiveData<List<Location>>()

    val updateFail: LiveData<UpdateFailType?> get() = _updateFail
    private val _updateFail = MutableLiveData<UpdateFailType?>()

    fun search(q: String) = viewModelScope.launch {
        when (val result = getLocationsByNameUseCase(q)) {
            is Success -> _searchResult.postValue(result.data)
            is Fail -> handleFailResult(result)
        }
    }

    fun saveLocation(location: Location) = viewModelScope.launch {
        saveLocationUseCase.invoke(location)
    }

    fun isLocationExist(location: Location): Boolean = runBlocking {
        getAllLocationsUseCase.invoke(Unit)
            .firstOrNull { it.url == location.url } != null
    }

    private fun handleFailResult(result: Fail<Any>) {
        when (result.exception) {
            is NetworkException -> _updateFail.postValue(UpdateFailType.FAIL_LOAD_FROM_NETWORK)
            else -> _updateFail.postValue(UpdateFailType.UNDEFINED)
        }
    }

    class Factory @Inject constructor(
        private val getLocationsByNameUseCase: GetLocationsByNameUseCase,
        private val saveLocationUseCase: SaveLocationUseCase,
        private val getAllLocationsUseCase: GetAllLocationsUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AddLocationViewModel(
                getLocationsByNameUseCase,
                saveLocationUseCase,
                getAllLocationsUseCase
            ) as T
        }
    }
}
