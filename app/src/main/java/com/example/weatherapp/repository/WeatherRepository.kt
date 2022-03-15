package com.example.weatherapp.repository

import android.content.SharedPreferences
import com.example.weatherapp.data.remote.ForecastApi
import com.example.weatherapp.data.remote.model.WeatherForecast
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.ui.settings.SettingsFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class WeatherRepository(
    private val appPreferences: SharedPreferences,
    private val forecastApi: ForecastApi,
) {

    private val disposeBag = CompositeDisposable()

    fun loadForecast(
        q: String,
        onSuccess: Consumer<WeatherForecast>,
        onError: Consumer<Throwable>
    ) {
        val result = forecastApi.getForecast(q)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    fun loadForecasts(
        q: List<String>,
        onSuccess: Consumer<WeatherForecast>,
        onError: Consumer<Throwable>
    ) {
        val result = Observable.fromIterable(q.map { forecastApi.getForecast(it) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val res = it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onSuccess, onError)
                disposeBag.add(res)
            }

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

    fun dispose() {
        disposeBag.dispose()
    }
}
