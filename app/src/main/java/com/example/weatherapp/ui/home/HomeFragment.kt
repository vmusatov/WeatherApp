package com.example.weatherapp.ui.home

import android.content.Context
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
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.domain.model.*
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.UpdateFailType
import com.example.weatherapp.ui.home.adapter.DailyForecastListAdapter
import com.example.weatherapp.ui.home.adapter.HourlyForecastItemDecorator
import com.example.weatherapp.ui.home.adapter.HourlyForecastListAdapter
import com.example.weatherapp.ui.home.adapter.NotificationsPagerAdapter
import com.example.weatherapp.ui.navigator
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = checkNotNull(_binding)

    @Inject
    lateinit var factory: HomeViewModel.Factory
    private val viewModel: HomeViewModel by activityViewModels { factory }

    private lateinit var hourlyForecastAdapter: HourlyForecastListAdapter
    private lateinit var dailyForecastAdapter: DailyForecastListAdapter
    private lateinit var notificationsAdapter: NotificationsPagerAdapter

    private var graphDecorator: RecyclerView.ItemDecoration? = null

    private val disableSwipeToUpdateRVScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!binding.refreshLayout.isRefreshing) {
                when (newState) {
                    SCROLL_STATE_DRAGGING -> binding.refreshLayout.isEnabled = false
                    else -> binding.refreshLayout.isEnabled = true
                }
            }
        }
    }

    private val disableSwipeToUpdateVPPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (!binding.refreshLayout.isRefreshing) {
                    binding.refreshLayout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
                }
            }
        }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hourlyForecastAdapter = HourlyForecastListAdapter()
        dailyForecastAdapter = DailyForecastListAdapter()
        notificationsAdapter = NotificationsPagerAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupObservers()
        setupUi()

        if (viewModel.selectedLocation.value == null) {
            viewModel.updateWeather()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.selectedLocation.observe(viewLifecycleOwner) {
            navigator().setToolbarTitle(it?.name ?: "")
        }
        viewModel.weatherData.observe(viewLifecycleOwner) { updateWeather(it) }
        viewModel.weatherNotifications.observe(viewLifecycleOwner) { updateNotifications(it) }
        viewModel.isUpdateInProgress.observe(viewLifecycleOwner) { showIsUpdate(it) }
        viewModel.updateFail.observe(viewLifecycleOwner) { handleError(it) }
    }

    private fun setupUi() = with(binding) {
        setupToolbar()

        hourlyForecast.adapter = hourlyForecastAdapter
        hourlyForecast.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        hourlyForecast.addOnScrollListener(disableSwipeToUpdateRVScrollListener)
        hourlyForecast.itemAnimator = null

        dailyForecast.adapter = dailyForecastAdapter
        dailyForecast.layoutManager = LinearLayoutManager(requireContext())

        notifications.notificationsPager.adapter = notificationsAdapter
        notifications.notificationsPager.registerOnPageChangeCallback(
            disableSwipeToUpdateVPPageChangeCallback
        )
        TabLayoutMediator(
            notifications.tabLayout,
            notifications.notificationsPager
        ) { _, _ -> }.attach()

        refreshLayout.setOnRefreshListener { viewModel.updateWeather(force = true) }
        errors.addLocation.setOnClickListener { navigator().goToAddLocation() }
    }

    private fun updateWeather(data: WeatherData) = with(binding) {
        val tempUnit = viewModel.getTempUnit()

        updateCurrentWeather(data.current, tempUnit)
        updateAdditionalWeather(data.current)
        updateAirQuality(data.current)
        updateAstronomy(data.current.astronomy)

        updateHourlyForecast(data, tempUnit)
        dailyForecastAdapter.update(data.daysForecast, tempUnit)

        updateFooter(data)
    }

    private fun updateNotifications(notificationsList: List<WeatherNotification>) = with(binding) {
        if (notificationsList.isEmpty()) {
            notifications.root.visibility = View.GONE
        } else {
            notificationsAdapter.update(notificationsList)
            notifications.root.visibility = View.VISIBLE
            if (notificationsList.size == 1) {
                notifications.tabLayout.visibility = View.GONE
            } else {
                notifications.tabLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCurrentWeather(currentWeather: CurrentWeather, tempUnit: TempUnit) =
        with(binding) {
            current.conditionText.text = currentWeather.conditionText

            Picasso.get()
                .load(currentWeather.conditionIcon)
                .into(current.currentConditionImage)

            if (tempUnit == TempUnit.C) {
                current.currentTemp.text = getString(R.string.degree, currentWeather.tempC.toInt())
                current.feelLikeTemp.text =
                    getString(R.string.feels_like, currentWeather.feelsLikeTempC.toInt())
            } else {
                current.currentTemp.text = getString(R.string.degree, currentWeather.tempF.toInt())
                current.feelLikeTemp.text =
                    getString(R.string.feels_like, currentWeather.feelsLikeTempF.toInt())
            }
        }

    private fun updateAdditionalWeather(current: CurrentWeather) = with(binding) {
        additionalWeather.pressureText.text = getString(R.string.mbar, current.pressureMb.toInt())
        additionalWeather.windText.text = getString(R.string.kmh, current.windKph.toInt())
        additionalWeather.uvIndexText.text =
            viewModel.parseUvIndex(requireContext(), current.uvIndex)
    }

    private fun updateAirQuality(current: CurrentWeather) = with(binding) {
        airQuality.coValue.text = current.co.toInt().toString()
        airQuality.no2Value.text = current.no2.toInt().toString()
        airQuality.o3Value.text = current.o3.toInt().toString()
        airQuality.so2Value.text = current.so2.toInt().toString()
        airQuality.usEpaIndexValue.text =
            viewModel.parseEpaIndex(requireContext(), current.usEpaIndex)
    }

    private fun updateHourlyForecast(data: WeatherData, tempUnit: TempUnit) = with(binding) {
        graphDecorator?.let { hourlyForecast.removeItemDecoration(it) }

        val hours = data.hoursForecast
        if (hours.isEmpty()) {
            hourlyForecast.visibility = View.GONE
        } else {
            hourlyForecastAdapter.update(hours, tempUnit)
            graphDecorator = HourlyForecastItemDecorator(hours.map { it.tempF }, requireContext())
            hourlyForecast.addItemDecoration(graphDecorator as HourlyForecastItemDecorator)

            hourlyForecast.scrollToPosition(0)
            hourlyForecast.visibility = View.VISIBLE
        }
    }

    private fun updateAstronomy(astronomyData: Astronomy) = with(binding) {
        astronomy.sunriseText.text = astronomyData.sunrise
        astronomy.sunsetText.text = astronomyData.sunset
    }

    private fun updateFooter(data: WeatherData) = with(binding) {
        footer.updatedAt.visibility = View.GONE
        data.lastUpdated?.let {
            footer.updatedAt.text = getString(R.string.updated_at, it)
            footer.updatedAt.visibility = View.VISIBLE
        }
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

    private fun handleError(type: UpdateFailType?) = with(binding) {
        refreshLayout.isEnabled = true

        if (type == null) {
            errors.root.visibility = View.GONE
            return
        }

        content.visibility = View.GONE
        errors.root.visibility = View.VISIBLE

        errors.addLocation.visibility = View.GONE

        errors.errorText.text = when (type) {
            UpdateFailType.FAIL_LOAD_FROM_DB -> getString(R.string.db_fail)
            UpdateFailType.FAIL_LOAD_FROM_NETWORK -> getString(R.string.network_fail)
            UpdateFailType.NO_LOCATION -> {
                refreshLayout.isEnabled = false
                errors.addLocation.visibility = View.VISIBLE
                getString(R.string.no_selected_location)
            }
            UpdateFailType.UNDEFINED -> getString(R.string.undefined_fail)
        }
    }

    private fun showIsUpdate(isUpdateInProgress: Boolean) = with(binding) {
        if (isUpdateInProgress) {
            errors.root.visibility = View.GONE
            refreshLayout.isRefreshing = true

            if (viewModel.weatherData.value == null) {
                content.visibility = View.INVISIBLE
            }
        } else {
            refreshLayout.isRefreshing = false
            if (viewModel.updateFail.value == null) {
                content.visibility = View.VISIBLE
            }
        }
    }
}