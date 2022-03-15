package com.example.weatherapp.repository

import com.example.weatherapp.data.remote.AstronomyApi
import com.example.weatherapp.data.remote.model.Astronomy
import com.example.weatherapp.util.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*

class AstronomyRepository(
    private val astronomyApi: AstronomyApi
) {
    private val disposeBag = CompositeDisposable()

    fun loadAstronomy(
        q: String,
        onSuccess: Consumer<Astronomy>,
        onError: Consumer<Throwable>
    ) {
        val result = astronomyApi.getAstronomy(q, DateUtils.dateFormat.format(Date()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        disposeBag.add(result)
    }

    fun dispose() {
        disposeBag.dispose()
    }
}