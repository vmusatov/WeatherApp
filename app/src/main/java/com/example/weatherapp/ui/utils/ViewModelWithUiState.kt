package com.example.weatherapp.ui.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class ViewModelWithUiState<T> : ViewModel() {
    val uiState: LiveData<T> get() = _uiState
    private val _uiState = MutableLiveData<T>()

    protected val uiStateValue get() = _uiState.value

    protected abstract fun createDefaultState(): T

    protected fun updateUiState(updater: (T) -> T) {
        _uiState.postValue(
            _uiState.value?.let { updater(it) }
                ?: updater(createDefaultState())
        )
    }
}