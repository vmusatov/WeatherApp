package com.example.weatherapp.repository

import android.content.SharedPreferences
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.model.Astronomy
import com.example.weatherapp.data.remote.model.WeatherForecast
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.ui.settings.SettingsFragment
import com.example.weatherapp.util.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class WeatherRepository(
    private val appPreferences: SharedPreferences,
    private val weatherApi: WeatherApi,
) {

    private val disposeBag = CompositeDisposable()

    fun loadForecast(
        q: String,
        onSuccess: Consumer<WeatherForecast>,
        onError: Consumer<Throwable>
    ) {
        val result = weatherApi.getForecast(q)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    suspend fun loadForecasts(q: List<String>): List<WeatherForecast> {
        return withContext(Dispatchers.IO) {
            q.map { weatherApi.getForecast(it).blockingGet() }
        }
    }

    fun loadAstronomy(
        q: String,
        onSuccess: Consumer<Astronomy>,
        onError: Consumer<Throwable>
    ) {
        val result = weatherApi.getAstronomy(q, DateUtils.dateFormat.format(Date()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    fun getTempUnit(): TempUnit {
        return TempUnit.fromCode(
            appPreferences.getInt(SettingsFragment.PREF_TEMP_CODE, TempUnit.DEFAULT.code)
        ) ?: TempUnit.DEFAULT
    }

    fun saveTempUnit(tempUnit: TempUnit) {
        appPreferences.edit()
            .putInt(SettingsFragment.PREF_TEMP_CODE, tempUnit.code)
            .apply()
    }

    fun clear() {
        disposeBag.clear()
    }
}
