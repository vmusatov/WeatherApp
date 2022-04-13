package com.example.weatherapp.domain.utils

sealed class WorkResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : WorkResult<T>()
    data class Fail<out T : Any>(val exception: Exception) : WorkResult<T>()

    fun <E : Any> map(mapper: (t: T) -> E): WorkResult<E> {
        return when(this) {
            is Success -> Success(mapper.invoke(data))
            is Fail -> Fail(exception)
        }
    }
}
