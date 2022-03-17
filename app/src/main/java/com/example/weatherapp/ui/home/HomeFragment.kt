package com.example.weatherapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.model.WeatherCurrent
import com.example.weatherapp.databinding.*
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.notification.WeatherNotification
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.navigator
import com.example.weatherapp.ui.viewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels { viewModelFactory() }

    private lateinit var hourlyForecastAdapter: HourlyForecastListAdapter
    private lateinit var dailyForecastAdapter: DailyForecastListAdapter
    private lateinit var notificationsAdapter: NotificationsPagerAdapter

    private var graphDecorator: RecyclerView.ItemDecoration? = null

    private lateinit var blockCurrent: BlockCurrentBinding
    private lateinit var blockAdditional: BlockAdditionalWeatherBinding
    private lateinit var blockAirQuality: BlockAirQualityBinding
    private lateinit var blockAstronomy: BlockAstronomyBinding
    private lateinit var blockNotifications: BlockNotificationsBinding

    private lateinit var hourlyForecastList: RecyclerView
    private lateinit var dailyForecastList: RecyclerView

    private val disableSwipeToUpdateListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                SCROLL_STATE_DRAGGING -> binding.refreshLayout.isEnabled = false
                else -> binding.refreshLayout.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupFields()
        setupObservers()
        setupUi()

        return binding.root
    }

    private fun setupFields() {
        hourlyForecastAdapter = HourlyForecastListAdapter()
        dailyForecastAdapter = DailyForecastListAdapter()
        notificationsAdapter = NotificationsPagerAdapter()

        blockCurrent = binding.current
        blockAdditional = binding.additionalWeather
        blockAirQuality = binding.airQuality
        blockAstronomy = binding.astronomy

        hourlyForecastList = binding.byHourList
        dailyForecastList = binding.byDayList
        blockNotifications = binding.notifications
    }

    private fun setupObservers() {
        viewModel.weatherForecast.observe(viewLifecycleOwner) { updateWeather() }
        viewModel.hourlyForecast.observe(viewLifecycleOwner) { updateHourlyForecast() }
        viewModel.astronomy.observe(viewLifecycleOwner) { updateAstronomy() }
        viewModel.weatherNotifications.observe(viewLifecycleOwner) { updateNotifications(it) }
        viewModel.isUpdateInProgress.observe(viewLifecycleOwner) { showIsUpdate(it) }
    }

    private fun setupUi() {
        setupToolbar()

        val lm = LinearLayoutManager(requireContext())
        lm.orientation = RecyclerView.HORIZONTAL

        hourlyForecastList.adapter = hourlyForecastAdapter
        hourlyForecastList.layoutManager = lm
        hourlyForecastList.addOnScrollListener(disableSwipeToUpdateListener)
        hourlyForecastList.scrollToPosition(0)

        dailyForecastList.adapter = dailyForecastAdapter
        dailyForecastList.layoutManager = LinearLayoutManager(requireContext())

        blockNotifications.notificationsPager.adapter = notificationsAdapter

        TabLayoutMediator(
            blockNotifications.tabLayout,
            blockNotifications.notificationsPager
        ) { _, _ -> }.attach()

        binding.refreshLayout.setOnRefreshListener { viewModel.updateAll() }
    }

    private fun updateWeather() {
        viewModel.weatherForecast.value?.let {
            val tempUnit = viewModel.getTempUnit()

            updateCurrentWeather(it.current, tempUnit)
            updateAdditionalWeather(it.current)
            updateAirQuality(it.current)

            dailyForecastAdapter.update(it.forecast.forecastDays, tempUnit)
        }
    }

    private fun updateNotifications(notifications: List<WeatherNotification>) {
        if (notifications.isEmpty()) {
            blockNotifications.root.visibility = View.GONE
        } else {
            notificationsAdapter.update(notifications)
            blockNotifications.root.visibility = View.VISIBLE
            if (notifications.size == 1) {
                blockNotifications.tabLayout.visibility = View.GONE
            } else {
                blockNotifications.tabLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCurrentWeather(current: WeatherCurrent, tempUnit: TempUnit) {
        blockCurrent.conditionText.text = current.condition.text

        Picasso.get()
            .load(current.condition.icon)
            .into(blockCurrent.currentConditionImage)

        if (tempUnit == TempUnit.C) {
            blockCurrent.currentTemp.text = getString(R.string.degree, current.tempC.toInt())
            blockCurrent.feelLikeTemp.text =
                getString(R.string.feels_like, current.feelsLikeTempC.toInt())
        } else {
            blockCurrent.currentTemp.text = getString(R.string.degree, current.tempF.toInt())
            blockCurrent.feelLikeTemp.text =
                getString(R.string.feels_like, current.feelsLikeTempF.toInt())
        }
    }

    private fun updateAdditionalWeather(current: WeatherCurrent) {
        blockAdditional.humidityText.text = current.humidity.toString() + "%"
        blockAdditional.windText.text = getString(R.string.kmh, current.windKph.toInt())
        blockAdditional.uvIndexText.text = viewModel.parseUvIndex(requireContext(), current.uvIndex)
    }

    private fun updateAirQuality(current: WeatherCurrent) {
        blockAirQuality.coValue.text = current.airQuality.co.toInt().toString()
        blockAirQuality.no2Value.text = current.airQuality.no2.toInt().toString()
        blockAirQuality.o3Value.text = current.airQuality.o3.toInt().toString()
        blockAirQuality.so2Value.text = current.airQuality.so2.toInt().toString()
        blockAirQuality.usEpaIndexValue.text =
            viewModel.parseEpaIndex(requireContext(), current.airQuality.usEpaIndex)
    }

    private fun updateHourlyForecast() {
        graphDecorator?.let { hourlyForecastList.removeItemDecoration(it) }

        viewModel.hourlyForecast.value?.let {
            if (it.isEmpty()) {
                hourlyForecastList.visibility = View.GONE
                binding.byHourListEmpty.visibility = View.VISIBLE
            } else {
                graphDecorator = HourlyForecastItemDecorator(it.map { it.tempF }, requireContext())
                hourlyForecastList.addItemDecoration(graphDecorator as HourlyForecastItemDecorator)
                hourlyForecastAdapter.update(it, viewModel.getTempUnit())

                hourlyForecastList.visibility = View.VISIBLE
                binding.byHourListEmpty.visibility = View.GONE
            }
        }
    }

    private fun updateAstronomy() {
        blockAstronomy.sunriseText.text = viewModel.astronomy.value?.sunrise
        blockAstronomy.sunsetText.text = viewModel.astronomy.value?.sunset
    }

    private fun setupToolbar() {
        navigator().setToolbarTitle(viewModel.getSelectedLocation()?.name ?: "")
        navigator().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_hamburger_menu,
                onAction = { navigator().goToManageLocations() }
            )
        )
        navigator().setToolbarRightAction(
            ToolbarAction(
                iconRes = R.drawable.ic_settings,
                onAction = { navigator().openSettings() }
            )
        )
    }

    private fun showIsUpdate(isUpdateInProgress: Boolean) {
        if (isUpdateInProgress) {
            binding.refreshLayout.isRefreshing = true
            if (viewModel.weatherForecast.value == null) {
                binding.content.visibility = View.INVISIBLE
            }
        } else {
            binding.refreshLayout.isRefreshing = false
            binding.content.visibility = View.VISIBLE
        }
    }
}