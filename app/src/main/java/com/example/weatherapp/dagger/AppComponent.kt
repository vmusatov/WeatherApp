package com.example.weatherapp.dagger

import android.content.Context
import com.example.weatherapp.ui.home.HomeFragment
import com.example.weatherapp.ui.locations.AddLocationFragment
import com.example.weatherapp.ui.locations.ManageLocationsFragment
import com.example.weatherapp.ui.settings.SettingsFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RoomModule::class,
        NotificationsModule::class
    ]
)
interface AppComponent {

    fun inject(homeFragment: HomeFragment)

    fun inject(addLocationFragment: AddLocationFragment)

    fun inject(manageLocationsFragment: ManageLocationsFragment)

    fun inject(settingsFragment: SettingsFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}

