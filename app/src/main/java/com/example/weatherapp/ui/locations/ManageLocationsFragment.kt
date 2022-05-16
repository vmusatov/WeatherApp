package com.example.weatherapp.ui.locations

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.weatherapp.R
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentManageLocationsBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.ui.locations.adapter.ManageLocationListAdapter
import com.example.weatherapp.ui.locations.util.LocationsChangeCallback
import com.example.weatherapp.ui.locations.util.LocationsItemTouchHelperCallback
import com.example.weatherapp.ui.utils.ToolbarAction
import com.example.weatherapp.ui.utils.toolbarManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class ManageLocationsFragment : Fragment() {

    private var _binding: FragmentManageLocationsBinding? = null
    private val binding: FragmentManageLocationsBinding get() = checkNotNull(_binding)

    @Inject
    lateinit var manageViewModelFactory: ManageLocationsViewModel.Factory
    private val viewModel: ManageLocationsViewModel by activityViewModels { manageViewModelFactory }

    private var manageLocationAdapter: ManageLocationListAdapter? = null
    private var touchHelper: ItemTouchHelper? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manageLocationAdapter = ManageLocationListAdapter(LocationsChangeCallbackImpl())
        touchHelper = ItemTouchHelper(
            LocationsItemTouchHelperCallback(requireContext(), manageLocationAdapter!!)
        )
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

    override fun onDestroy() {
        super.onDestroy()
        manageLocationAdapter = null
        touchHelper = null
    }

    private fun setupUi() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            manageLocationAdapter?.tempUnit = it.tempUnit
            updateLocationsList(it.locations)
            manageLocationAdapter?.updateLocationsInfo(it.weatherData.toList())
        }
    }

    private fun setupRecyclerView() = with(binding) {
        touchHelper?.attachToRecyclerView(locationsList)

        delete.setOnClickListener { manageLocationAdapter?.deleteSelected() }
        apply.setOnClickListener { manageLocationAdapter?.switchEditMode() }

        with(locationsList) {
            adapter = manageLocationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        (locationsList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun updateLocationsList(locations: List<Location>) = with(binding) {
        if (locations.isEmpty()) {
            locationsList.visibility = View.GONE
            editBlock.visibility = View.GONE
            emptyLocations.visibility = View.VISIBLE
        } else {
            locationsList.visibility = View.VISIBLE
            emptyLocations.visibility = View.GONE
            manageLocationAdapter?.update(locations)
        }
    }

    private fun setupToolbar() {
        toolbarManager().clearToolbar()
        toolbarManager().setToolbarTitle(getString(R.string.manage_locations))
        toolbarManager().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_arrow_back,
                onAction = {
                    findNavController().navigateUp()
                }
            )
        )
        toolbarManager().setToolbarRightAction(
            ToolbarAction(
                iconRes = R.drawable.ic_search,
                onAction = {
                    findNavController().navigate(R.id.action_manageLocationsFragment_to_addLocationFragment)
                }
            )
        )
    }

    inner class LocationsChangeCallbackImpl : LocationsChangeCallback {

        override fun onSelectLocation(location: Location) {
            lifecycleScope.launch {
                viewModel.setLocationIsSelected(location).join()
                findNavController().navigateUp()
            }
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
            touchHelper?.startDrag(viewHolder)
        }
    }
}