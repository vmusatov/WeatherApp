package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.usecase.location.GetAllLocationsUseCase
import com.example.weatherapp.domain.usecase.location.GetLocationsByNameUseCase
import com.example.weatherapp.domain.usecase.location.SaveLocationUseCase
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

    fun search(q: String) = viewModelScope.launch {
        _searchResult.postValue(getLocationsByNameUseCase.invoke(q))
    }

    fun saveLocation(location: Location) = viewModelScope.launch {
        saveLocationUseCase.invoke(location)
    }

    fun isLocationExist(location: Location): Boolean = runBlocking {
        getAllLocationsUseCase.invoke(Unit)
            .firstOrNull { it.url == location.url } != null
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
