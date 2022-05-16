package com.example.weatherapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.R
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.domain.model.*
import com.example.weatherapp.ui.home.adapter.DailyForecastListAdapter
import com.example.weatherapp.ui.home.adapter.HourlyForecastItemDecorator
import com.example.weatherapp.ui.home.adapter.HourlyForecastListAdapter
import com.example.weatherapp.ui.home.adapter.NotificationsPagerAdapter
import com.example.weatherapp.ui.home.util.parseEpaIndex
import com.example.weatherapp.ui.home.util.parseUvIndex
import com.example.weatherapp.ui.utils.LoadErrorType
import com.example.weatherapp.ui.utils.ToolbarAction
import com.example.weatherapp.ui.utils.UiState
import com.example.weatherapp.ui.utils.toolbarManager
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = checkNotNull(_binding)

    @Inject
    lateinit var factory: HomeViewModel.Factory
    private val viewModel: HomeViewModel by viewModels { factory }

    private var hourlyForecastAdapter: HourlyForecastListAdapter? = null
    private var dailyForecastAdapter: DailyForecastListAdapter? = null
    private var notificationsAdapter: NotificationsPagerAdapter? = null

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

        setupUi()
        setupObservers()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        hourlyForecastAdapter = null
        dailyForecastAdapter = null
        notificationsAdapter = null

        graphDecorator = null
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state.uiState) {
                UiState.LOADING -> showIsUpdate(true)

                UiState.LOAD_ERROR -> {
                    handleError(state.loadErrorType ?: LoadErrorType.UNDEFINED)
                    showIsUpdate(false)
                }

                UiState.READY_TO_SHOW -> {
                    state.weatherData?.let { data ->
                        updateWeather(data, state.tempUnit)
                        updateNotifications(state.weatherNotifications)
                    } ?: handleError(LoadErrorType.UNDEFINED)

                    showIsUpdate(false)
                }

                else -> {
                    showIsUpdate(false)
                }
            }
        }
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
        errors.addLocation.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addLocationFragment)
        }
    }

    private fun updateWeather(data: WeatherData, tempUnit: TempUnit) = with(binding) {
        toolbarManager().setToolbarTitle(data.location.name)

        updateCurrentWeather(data.current, tempUnit)
        updateAdditionalWeather(data.current)
        updateAirQuality(data.current)
        updateAstronomy(data.current.astronomy)

        updateHourlyForecast(data, tempUnit)
        dailyForecastAdapter?.update(data.daysForecast, tempUnit)

        updateFooter(data)

        errors.root.visibility = View.GONE
        content.visibility = View.VISIBLE
    }

    private fun updateNotifications(notificationsList: List<WeatherNotification>) = with(binding) {
        if (notificationsList.isEmpty()) {
            notifications.root.visibility = View.GONE
        } else {
            notificationsAdapter?.update(notificationsList)
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
        additionalWeather.uvIndexText.text = parseUvIndex(requireContext(), current.uvIndex)
    }

    private fun updateAirQuality(current: CurrentWeather) = with(binding) {
        airQuality.coValue.text = current.co.toInt().toString()
        airQuality.no2Value.text = current.no2.toInt().toString()
        airQuality.o3Value.text = current.o3.toInt().toString()
        airQuality.so2Value.text = current.so2.toInt().toString()
        airQuality.usEpaIndexValue.text = parseEpaIndex(requireContext(), current.usEpaIndex)
    }

    private fun updateHourlyForecast(data: WeatherData, tempUnit: TempUnit) = with(binding) {
        graphDecorator?.let { hourlyForecast.removeItemDecoration(it) }

        val hours = data.hoursForecast
        if (hours.isEmpty()) {
            hourlyForecast.visibility = View.GONE
        } else {
            hourlyForecastAdapter?.update(hours, tempUnit)
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

    private fun handleError(type: LoadErrorType) = with(binding) {
        content.visibility = View.GONE
        errors.root.visibility = View.VISIBLE

        errors.addLocation.visibility = View.GONE

        errors.errorText.text = when (type) {
            LoadErrorType.FAIL_LOAD_FROM_DB -> getString(R.string.db_fail)
            LoadErrorType.FAIL_LOAD_FROM_NETWORK -> getString(R.string.network_fail)
            LoadErrorType.NO_LOCATION -> {
                refreshLayout.isEnabled = false
                errors.addLocation.visibility = View.VISIBLE
                getString(R.string.no_selected_location)
            }
            LoadErrorType.UNDEFINED -> getString(R.string.undefined_fail)
        }
    }

    private fun showIsUpdate(isUpdateInProgress: Boolean) = with(binding) {
        if (isUpdateInProgress) {
            errors.root.visibility = View.GONE
            refreshLayout.isRefreshing = true

            if (viewModel.uiState.value?.weatherData == null) {
                content.visibility = View.INVISIBLE
            }
        } else {
            refreshLayout.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        toolbarManager().clearToolbar()
        toolbarManager().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_hamburger_menu,
                onAction = navigateToManageLocations
            )
        )
        toolbarManager().setToolbarRightAction(
            ToolbarAction(
                iconRes = R.drawable.ic_settings,
                onAction = navigateToSettings
            )
        )
    }

    private val navigateToManageLocations = {
        findNavController().navigate(
            R.id.action_homeFragment_to_manageLocationsFragment,
            bundleOf(),
            navOptions = navOptions {
                anim {
                    enter = R.anim.enter_from_left
                    exit = R.anim.exit_to_right
                    popEnter = R.anim.enter_from_right
                    popExit = R.anim.exit_to_left
                }
            }
        )
    }

    private val navigateToSettings = {
        findNavController().navigate(
            R.id.action_homeFragment_to_settingsFragment,
            bundleOf(),
            navOptions = navOptions {
                anim {
                    enter = R.anim.enter_from_right
                    exit = R.anim.exit_to_left
                    popEnter = R.anim.enter_from_left
                    popExit = R.anim.exit_to_right
                }
            }
        )
    }
}