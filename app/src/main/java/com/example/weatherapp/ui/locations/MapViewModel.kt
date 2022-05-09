package com.example.weatherapp.ui.locations

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.domain.usecase.weather.GetShortWeatherDataUseCase
import com.example.weatherapp.domain.utils.WorkResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel(
    private val getShortWeatherDataUseCase: GetShortWeatherDataUseCase
) : ViewModel() {

    val weatherData: LiveData<ShortWeatherData> get() = _weatherData
    private val _weatherData = MutableLiveData<ShortWeatherData>()

    fun loadWeatherData(location: Location) = viewModelScope.launch {
        val result = getShortWeatherDataUseCase.execute(location)

        if (result is WorkResult.Success) {
            _weatherData.postValue(result.data)
        }
    }

    class Factory @Inject constructor(
        private val getShortWeatherDataUseCase: GetShortWeatherDataUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MapViewModel(getShortWeatherDataUseCase) as T
        }
    }
}