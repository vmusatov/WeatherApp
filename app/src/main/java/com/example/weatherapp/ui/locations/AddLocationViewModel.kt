package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.usecase.location.GetLocationsByNameUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddLocationViewModel(
    private val getLocationsByNameUseCase: GetLocationsByNameUseCase
) : ViewModel() {

    val searchResult: LiveData<List<Location>> get() = _searchResult
    private val _searchResult = MutableLiveData<List<Location>>()

    fun search(q: String) = viewModelScope.launch {
        _searchResult.postValue(getLocationsByNameUseCase.invoke(q))
    }

    class Factory @Inject constructor(
        private val getLocationsByNameUseCase: GetLocationsByNameUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AddLocationViewModel(getLocationsByNameUseCase) as T
        }
    }
}
