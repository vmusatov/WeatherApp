package com.example.weatherapp.domain.usecase

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCase<T, E> : CoroutineScope {
    private val parentJob = SupervisorJob()
    private val mainDispatcher = Dispatchers.Main
    private val backgroundDispatcher = Dispatchers.Default

    override val coroutineContext: CoroutineContext
        get() = parentJob + mainDispatcher

    protected abstract suspend fun execute(data: T): E

    suspend operator fun invoke(data: T) = withContext(backgroundDispatcher) {
        execute(data)
    }
}