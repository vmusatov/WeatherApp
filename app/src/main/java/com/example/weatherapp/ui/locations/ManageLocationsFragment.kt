package com.example.weatherapp.ui.locations

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
import com.example.weatherapp.databinding.FragmentManageLocationsBinding
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.home.HomeViewModel
import com.example.weatherapp.ui.navigator
import com.example.weatherapp.ui.viewModelFactory

class ManageLocationsFragment : Fragment() {

    private lateinit var binding: FragmentManageLocationsBinding
    private val viewModel: ManageLocationsViewModel by activityViewModels { viewModelFactory() }
    private val homeViewModel: HomeViewModel by activityViewModels { viewModelFactory() }

    private lateinit var manageLocationAdapter: ManageLocationListAdapter
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageLocationsBinding.inflate(inflater, container, false)

        setupFields()
        setupUi()
        setupObservers()

        viewModel.updateLocations()

        return binding.root
    }

    private fun setupFields() {
        manageLocationAdapter = ManageLocationListAdapter(LocationsManagerImpl())

        touchHelper = ItemTouchHelper(
            LocationsItemTouchHelperCallback(requireContext(), manageLocationAdapter)
        )
    }

    private fun setupUi() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupObservers() {
        viewModel.locations.observe(viewLifecycleOwner) {
            manageLocationAdapter.update(it)
        }
        viewModel.locationsWeatherInfo.observe(viewLifecycleOwner) {
            manageLocationAdapter.updateLocationsInfo(it)
        }
    }

    private fun setupRecyclerView() {
        touchHelper.attachToRecyclerView(binding.locationsList)

        binding.delete.setOnClickListener {
            manageLocationAdapter.deleteSelected()
        }

        binding.apply.setOnClickListener {
            manageLocationAdapter.switchEditMode()
        }

        manageLocationAdapter.tempUnit = homeViewModel.getTempUnit()

        with(binding.locationsList) {
            adapter = manageLocationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupToolbar() {
        navigator().setToolbarTitle(requireContext().getString(R.string.manage_locations))
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

    inner class LocationsManagerImpl : LocationsManager {

        override fun onSelectLocation(location: LocationDto) {
            homeViewModel.updateWeather(location)
            navigator().goBack()
        }

        override fun onSwitchEditMode(editMode: Boolean) {
            if (editMode) {
                binding.editBlock.visibility = View.VISIBLE
            } else {
                binding.editBlock.visibility = View.GONE
            }
        }

        override fun onDeleteLocations(locations: List<LocationDto>) {
            locations.forEach { viewModel.removeLocation(it) }
        }

        override fun onApplyChanges(locations: List<LocationDto>) {
            locations.forEachIndexed { index, location ->
                viewModel.updateLocationPosition(location, index)
            }
        }

        override fun onDragStart(viewHolder: RecyclerView.ViewHolder) {
            touchHelper.startDrag(viewHolder)
        }
    }
}