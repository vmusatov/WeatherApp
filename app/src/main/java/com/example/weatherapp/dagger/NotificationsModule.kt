package com.example.weatherapp.dagger

import com.example.weatherapp.notification.WeatherNotificationsBuilder
import com.example.weatherapp.notification.factory.*
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import javax.inject.Singleton

@Module
class NotificationsModule {

    @Provides
    fun provideWeatherNotificationsBuilder(
        factories: Set<@JvmSuppressWildcards WeatherNotificationFactory>
    ): WeatherNotificationsBuilder {
        return WeatherNotificationsBuilder(factories)
    }

    @Provides
    @ElementsIntoSet
    fun provideNotificationsFactories(
        noPrecipitationsFactory: NoPrecipitationsFactory,
        expectPrecipitationsFactory: ExpectPrecipitationsFactory,
        expectPrecipitationsEndFactory: ExpectPrecipitationsEndFactory,
        tempTomorrowFactory: TempTomorrowFactory
    ): Set<WeatherNotificationFactory> = setOf(
        noPrecipitationsFactory,
        expectPrecipitationsFactory,
        expectPrecipitationsEndFactory,
        tempTomorrowFactory
    )
}