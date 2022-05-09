package com.example.weatherapp.domain.usecase

interface BaseUseCase<T, E> {
    suspend fun execute(data: T): E
}