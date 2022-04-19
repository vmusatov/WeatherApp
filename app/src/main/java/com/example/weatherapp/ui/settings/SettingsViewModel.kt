package com.example.weatherapp.ui.settings

import androidx.lifecycle.*
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.domain.usecase.settings.GetTempUnitUseCase
import com.example.weatherapp.domain.usecase.settings.SaveTempUnitUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel(
    private val getTempUnitUseCase: GetTempUnitUseCase,
    private val saveTempUnitUseCase: SaveTempUnitUseCase,
) : ViewModel() {

    val tempUnit: LiveData<TempUnit> get() = _tempUnit
    private val _tempUnit = MutableLiveData<TempUnit>()

    init {
        loadTempUnit()
    }

    private fun loadTempUnit() = viewModelScope.launch {
         _tempUnit.postValue(getTempUnitUseCase.invoke(Unit))
    }

    fun getTempUnit(): TempUnit {
        return _tempUnit.value ?: TempUnit.DEFAULT
    }

    fun saveTempUnit(tempUnit: TempUnit) = viewModelScope.launch {
        _tempUnit.value = tempUnit
        saveTempUnitUseCase(tempUnit)
    }

    class Factory @Inject constructor(
        private val getTempUnitUseCase: GetTempUnitUseCase,
        private val saveTempUnitUseCase: SaveTempUnitUseCase,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(getTempUnitUseCase, saveTempUnitUseCase) as T
        }
    }
}