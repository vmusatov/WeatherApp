package com.example.weatherapp.ui

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.ui.home.HomeViewModel
import com.example.weatherapp.ui.locations.AddLocationViewModel
import com.example.weatherapp.ui.locations.ManageLocationsViewModel

class ViewModelFactory(
    private val app: WeatherApp
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(app.weatherRepository, app.locationRepository, app.astronomyRepository)
            AddLocationViewModel::class.java -> AddLocationViewModel(app.searchApi, app.locationRepository)
            ManageLocationsViewModel::class.java -> ManageLocationsViewModel(app.locationRepository)
            else -> throw IllegalArgumentException("Unknown view model class " + modelClass.name)
        }
        return viewModel as T
    }
}

data class FragmentAnimation(
    @AnimatorRes @AnimRes val enter: Int,
    @AnimatorRes @AnimRes val exit: Int,
    @AnimatorRes @AnimRes val popEnter: Int,
    @AnimatorRes @AnimRes val popExit: Int
)

fun Fragment.viewModelFactory() = ViewModelFactory(requireContext().applicationContext as WeatherApp)

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}