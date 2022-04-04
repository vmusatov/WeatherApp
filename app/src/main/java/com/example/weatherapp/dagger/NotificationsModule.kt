package com.example.weatherapp.dagger

import com.example.weatherapp.notification.factory.*
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet

@Module
class NotificationsModule {

    @Provides
    @ElementsIntoSet
    fun provideNotificationsFactories(
        noPrecipitationsFactory: NoPrecipitationsFactory,
        expectPrecipitationsFactory: ExpectPrecipitationsFactory,
        expectPrecipitationsEndFactory: ExpectPrecipitationsEndFactory,
        tempTomorrowFactory: TempTomorrowFactory
    ): Set<WeatherNotificationFactory> = linkedSetOf(
        noPrecipitationsFactory,
        expectPrecipitationsFactory,
        expectPrecipitationsEndFactory,
        tempTomorrowFactory
    )
}