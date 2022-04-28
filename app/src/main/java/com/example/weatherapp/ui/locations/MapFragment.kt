package com.example.weatherapp.ui.locations

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.ShortWeatherData
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.UpdateFailType
import com.example.weatherapp.ui.showShortToast
import com.example.weatherapp.ui.toolbarManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.IconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import javax.inject.Inject

class MapFragment : Fragment() {

    private val DEFAULT_MAP_ZOOM_VALUE = 14.0
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = checkNotNull(_binding)

    @Inject
    lateinit var viewModelFactory: MapViewModel.Factory
    private val viewModel: MapViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var addLocationsFactory: AddLocationViewModel.Factory
    private val addViewModel: AddLocationViewModel by viewModels { addLocationsFactory }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private val iconOverlay = IconOverlay()

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        setupToolbar()

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        setupMap()
        setupObservers()
        setupListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDetach()
        _binding = null
    }

    private fun setupListeners() {
        binding.closePopup.setOnClickListener {
            binding.popup.visibility = View.GONE
            binding.map.overlays.remove(iconOverlay)
            binding.map.overlayManager.onResume()
        }
        binding.addLocation.setOnClickListener { addLocation(it.tag as Location) }
    }

    private fun setupObservers() {
        viewModel.weatherData.observe(viewLifecycleOwner) { updateLocationWeather(it) }
        addViewModel.updateFail.observe(viewLifecycleOwner) { handleError(it) }
        addViewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showPopup(it[0])
            } else {
                binding.popup.visibility = View.GONE
                showShortToast(requireContext().getString(R.string.nothing_found))
            }
        }
    }

    private fun setupMap() = with(binding) {
        map.setUseDataConnection(true)
        map.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

        map.setMultiTouchControls(true)

        val locationOverlay = MyLocationNewOverlay(map)

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        map.overlays.add(locationOverlay)
        map.overlays.add(MapEventsOverlay(MapEventsReceiverImpl()))

        val controller = map.controller
        controller.setZoom(DEFAULT_MAP_ZOOM_VALUE)

        locationOverlay.runOnFirstFix {
            coroutineScope.launch {
                controller.animateTo(locationOverlay.myLocation, DEFAULT_MAP_ZOOM_VALUE, 0)
            }
        }

        Configuration.getInstance().load(requireContext(), sharedPreferences)
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun showPopup(location: Location) = with(binding) {
        viewModel.loadWeatherData(location)

        popupContent.visibility = View.VISIBLE

        locationName.text = location.name
        locationDesc.text = "${location.region}, ${location.country}"

        addLocation.tag = location

        errorText.visibility = View.GONE
        popup.visibility = View.VISIBLE
    }

    private fun updateLocationWeather(it: ShortWeatherData) = with(binding) {
        locationTemp.text = requireContext().getString(R.string.degree, it.tempC.toInt())
        Picasso.get()
            .load(it.conditionIconUrl)
            .into(locationCondition)
    }

    private fun addLocation(location: Location) {
        if (addViewModel.isLocationExist(location)) {
            showShortToast(getString(R.string.location_alredy_added))
        } else {
            coroutineScope.launch {
                addViewModel.saveLocation(location).join()
                findNavController().popBackStack(R.id.addLocationFragment, true)
            }
        }
    }

    private fun checkPermission(permission: String) {
        val checkSelfPermission = ContextCompat.checkSelfPermission(requireContext(), permission)

        if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun handleError(type: UpdateFailType?) = with(binding) {
        when (type) {
            UpdateFailType.FAIL_LOAD_FROM_NETWORK -> {
                errorText.text = getString(R.string.network_fail)
            }
            else -> errorText.text = getString(R.string.undefined_fail)
        }

        popupContent.visibility = View.GONE
        errorText.visibility = View.VISIBLE

        popup.visibility = View.VISIBLE
    }

    private fun setupToolbar() {
        toolbarManager().clearToolbar()
        toolbarManager().setToolbarTitle(requireContext().getString(R.string.add_location))
        toolbarManager().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_arrow_back,
                onAction = { findNavController().navigateUp() }
            )
        )
    }

    inner class MapEventsReceiverImpl : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            val locationsIcon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_location_selected
            )

            if (!binding.map.overlays.contains(iconOverlay)) {
                binding.map.overlays.add(iconOverlay)
            }
            iconOverlay.set(p, locationsIcon)
            iconOverlay.moveTo(p, binding.map)

            addViewModel.search("${p?.latitude} ${p?.longitude}")
            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean {
            return true
        }
    }
}