package com.example.weatherapp.data.utils

import com.example.weatherapp.exception.DbException
import com.example.weatherapp.exception.NetworkException
import com.example.weatherapp.exception.NotFoundException
import com.example.weatherapp.exception.UndefinedException
import com.example.weatherapp.domain.utils.WorkResult
import retrofit2.Response
import java.io.IOException

suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): WorkResult<T> {
    try {
        val response = call.invoke()

        if (response.isSuccessful) {
            return WorkResult.Success(response.body()!!)
        }

        return WorkResult.Fail(UndefinedException)

    } catch (e: Exception) {
        return when (e) {
            is IOException -> WorkResult.Fail(NetworkException)
            else -> WorkResult.Fail(UndefinedException)
        }
    }
}

suspend fun <T : Any> safeDbCall(call: suspend () -> T?): WorkResult<T> {
    try {
        val data = call.invoke()

        return if (data == null) {
            WorkResult.Fail(NotFoundException)
        } else {
            WorkResult.Success(data)
        }

    } catch (e: Exception) {
        return WorkResult.Fail(DbException)
    }
}

