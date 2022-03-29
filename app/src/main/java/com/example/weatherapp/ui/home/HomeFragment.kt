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
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.R
import com.example.weatherapp.databinding.*
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.Astronomy
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.WeatherData
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

    private val disableSwipeToUpdateRVScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                SCROLL_STATE_DRAGGING -> binding.refreshLayout.isEnabled = false
                else -> binding.refreshLayout.isEnabled = true
            }
        }
    }

    private val disableSwipeToUpdateVPPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                binding.refreshLayout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
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
        viewModel.weatherData.observe(viewLifecycleOwner) {
            updateWeather(it)
            updateHourlyForecast(it)
        }
        viewModel.astronomy.observe(viewLifecycleOwner) { updateAstronomy(it) }
        viewModel.weatherNotifications.observe(viewLifecycleOwner) { updateNotifications(it) }
        viewModel.isUpdateInProgress.observe(viewLifecycleOwner) { showIsUpdate(it) }
        viewModel.selectedLocation.observe(viewLifecycleOwner) { navigator().setToolbarTitle(it.name) }
    }

    private fun setupUi() {
        setupToolbar()

        val lm = LinearLayoutManager(requireContext())
        lm.orientation = RecyclerView.HORIZONTAL

        hourlyForecastList.adapter = hourlyForecastAdapter
        hourlyForecastList.layoutManager = lm
        hourlyForecastList.addOnScrollListener(disableSwipeToUpdateRVScrollListener)
        hourlyForecastList.scrollToPosition(0)

        dailyForecastList.adapter = dailyForecastAdapter
        dailyForecastList.layoutManager = LinearLayoutManager(requireContext())

        blockNotifications.notificationsPager.adapter = notificationsAdapter
        blockNotifications.notificationsPager.registerOnPageChangeCallback(
            disableSwipeToUpdateVPPageChangeCallback
        )

        TabLayoutMediator(
            blockNotifications.tabLayout,
            blockNotifications.notificationsPager
        ) { _, _ -> }.attach()

        binding.refreshLayout.setOnRefreshListener { viewModel.updateWeather(force = true) }
    }

    private fun updateWeather(data: WeatherData) {
        val tempUnit = viewModel.getTempUnit()

        updateCurrentWeather(data.current, tempUnit)
        updateAdditionalWeather(data.current)
        updateAirQuality(data.current)

        dailyForecastAdapter.update(data.daysForecast, tempUnit)
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

    private fun updateCurrentWeather(current: CurrentWeather, tempUnit: TempUnit) {
        blockCurrent.conditionText.text = current.conditionText

        Picasso.get()
            .load(current.conditionIcon)
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

    private fun updateAdditionalWeather(current: CurrentWeather) {
        blockAdditional.pressureText.text = getString(R.string.mbar, current.pressureMb.toInt())
        blockAdditional.windText.text = getString(R.string.kmh, current.windKph.toInt())
        blockAdditional.uvIndexText.text = viewModel.parseUvIndex(requireContext(), current.uvIndex)
    }

    private fun updateAirQuality(current: CurrentWeather) {
        blockAirQuality.coValue.text = current.co.toInt().toString()
        blockAirQuality.no2Value.text = current.no2.toInt().toString()
        blockAirQuality.o3Value.text = current.o3.toInt().toString()
        blockAirQuality.so2Value.text = current.so2.toInt().toString()
        blockAirQuality.usEpaIndexValue.text =
            viewModel.parseEpaIndex(requireContext(), current.usEpaIndex)
    }

    private fun updateHourlyForecast(data: WeatherData) {
        graphDecorator?.let { hourlyForecastList.removeItemDecoration(it) }

        val hours = data.hoursForecast

        if (hours.isEmpty()) {
            hourlyForecastList.visibility = View.GONE
            binding.byHourListEmpty.visibility = View.VISIBLE
        } else {
            graphDecorator = HourlyForecastItemDecorator(hours.map { it.tempF }, requireContext())
            hourlyForecastList.addItemDecoration(graphDecorator as HourlyForecastItemDecorator)
            hourlyForecastAdapter.update(hours, viewModel.getTempUnit())

            hourlyForecastList.visibility = View.VISIBLE
            binding.byHourListEmpty.visibility = View.GONE
        }

    }

    private fun updateAstronomy(astronomy: Astronomy) {
        blockAstronomy.sunriseText.text = astronomy.sunrise
        blockAstronomy.sunsetText.text = astronomy.sunset
    }

    private fun setupToolbar() {
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
            if (viewModel.weatherData.value == null) {
                binding.content.visibility = View.INVISIBLE
            }
        } else {
            binding.refreshLayout.isRefreshing = false
            binding.content.visibility = View.VISIBLE
        }
    }
}