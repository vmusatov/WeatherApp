package com.example.weatherapp.ui.locations

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentManageLocationsBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.home.HomeViewModel
import com.example.weatherapp.ui.locations.adapter.ManageLocationListAdapter
import com.example.weatherapp.ui.locations.util.LocationsChangeCallback
import com.example.weatherapp.ui.locations.util.LocationsItemTouchHelperCallback
import com.example.weatherapp.ui.navigator
import javax.inject.Inject

class ManageLocationsFragment : Fragment() {

    private var _binding: FragmentManageLocationsBinding? = null
    private val binding: FragmentManageLocationsBinding get() = checkNotNull(_binding)

    @Inject
    lateinit var manageViewModelFactory: ManageLocationsViewModel.Factory
    private val viewModel: ManageLocationsViewModel by activityViewModels { manageViewModelFactory }

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val homeViewModel: HomeViewModel by activityViewModels { homeViewModelFactory }

    private lateinit var manageLocationAdapter: ManageLocationListAdapter
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manageLocationAdapter = ManageLocationListAdapter(LocationsChangeCallbackImpl())
        touchHelper = ItemTouchHelper(
            LocationsItemTouchHelperCallback(requireContext(), manageLocationAdapter)
        )
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageLocationsBinding.inflate(inflater, container, false)

        setupUi()
        setupObservers()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUi() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupObservers() {
        viewModel.locations.observe(viewLifecycleOwner) {
            updateLocationsList(it)
        }
        viewModel.locationsShortWeatherData.observe(viewLifecycleOwner) {
            manageLocationAdapter.updateLocationsInfo(it.toList())
        }
    }

    private fun setupRecyclerView() = with(binding) {
        touchHelper.attachToRecyclerView(locationsList)

        delete.setOnClickListener {
            manageLocationAdapter.deleteSelected()
        }

        apply.setOnClickListener {
            manageLocationAdapter.switchEditMode()
        }

        manageLocationAdapter.tempUnit = viewModel.getTempUnit()

        with(locationsList) {
            adapter = manageLocationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateLocationsList(locations: List<Location>) = with(binding) {
        if (locations.isEmpty()) {
            locationsList.visibility = View.GONE
            editBlock.visibility = View.GONE
            emptyLocations.visibility = View.VISIBLE
        } else {
            locationsList.visibility = View.VISIBLE
            emptyLocations.visibility = View.GONE
            manageLocationAdapter.update(locations)
        }
    }

    private fun setupToolbar() {
        navigator().setToolbarTitle(getString(R.string.manage_locations))
        navigator().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_arrow_back,
                onAction = {
                    homeViewModel.updateWeather()
                    navigator().goBack()
                }
            )
        )
        navigator().setToolbarRightAction(
            ToolbarAction(
                iconRes = R.drawable.ic_search,
                onAction = { navigator().goToAddLocation() }
            )
        )
    }

    inner class LocationsChangeCallbackImpl : LocationsChangeCallback {

        override fun onSelectLocation(location: Location) {
            homeViewModel.updateWeather(location)
            viewModel.updateWeatherInfo(listOf(location))
            navigator().goBack()
        }

        override fun onSwitchEditMode(editMode: Boolean) = with(binding) {
            if (editMode) {
                editBlock.visibility = View.VISIBLE
            } else {
                editBlock.visibility = View.GONE
            }
        }

        override fun onDeleteLocations(locations: List<Location>) {
            viewModel.removeLocations(locations)
        }

        override fun onApplyChanges(locations: List<Location>) {
            viewModel.updateLocationPositions(locations)
        }

        override fun onDragStart(viewHolder: RecyclerView.ViewHolder) {
            touchHelper.startDrag(viewHolder)
        }
    }
}