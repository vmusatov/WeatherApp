package com.example.weatherapp.domain.di

import com.example.weatherapp.domain.usecase.notification.*
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    @ElementsIntoSet
    fun provideNotificationsFactories(
        noPrecipitationsFactory: NoPrecipitationsUseCase,
        expectPrecipitationsFactory: ExpectPrecipitationsUseCase,
        expectPrecipitationsEndFactory: ExpectPrecipitationsEndUseCase,
        tempTomorrowFactory: TempTomorrowUseCase
    ): Set<WeatherNotificationUseCase> = linkedSetOf(
        noPrecipitationsFactory,
        expectPrecipitationsFactory,
        expectPrecipitationsEndFactory,
        tempTomorrowFactory
    )
}