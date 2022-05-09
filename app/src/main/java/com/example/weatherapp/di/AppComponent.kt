package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.data.di.RepositoryModule
import com.example.weatherapp.data.di.RetrofitModule
import com.example.weatherapp.data.di.RoomModule
import com.example.weatherapp.domain.di.DomainModule
import com.example.weatherapp.ui.home.HomeFragment
import com.example.weatherapp.ui.locations.AddLocationFragment
import com.example.weatherapp.ui.locations.ManageLocationsFragment
import com.example.weatherapp.ui.locations.MapFragment
import com.example.weatherapp.ui.settings.SettingsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RetrofitModule::class,
        RoomModule::class,
        RepositoryModule::class,
        DomainModule::class,
        CoroutinesModule::class
    ]
)

interface AppComponent {

    fun inject(homeFragment: HomeFragment)

    fun inject(addLocationFragment: AddLocationFragment)

    fun inject(manageLocationsFragment: ManageLocationsFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(mapFragment: MapFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}

