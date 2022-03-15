package com.example.weatherapp.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentManageLocationsBinding
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.home.HomeViewModel
import com.example.weatherapp.ui.navigator
import com.example.weatherapp.ui.viewModelFactory

class ManageLocationsFragment : Fragment() {

    private lateinit var binding: FragmentManageLocationsBinding
    private val viewModel: ManageLocationsViewModel by activityViewModels { viewModelFactory() }
    private val homeViewModel: HomeViewModel by activityViewModels { viewModelFactory() }

    private lateinit var manageLocationAdapter: ManageLocationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageLocationsBinding.inflate(inflater, container, false)

        setupToolbar()
        setupUi()

        return binding.root
    }

    private fun setupUi() {
        setupToolbar()

        manageLocationAdapter = ManageLocationListAdapter {
            viewModel.setLocationIsSelected(it)
            homeViewModel.updateForecast()
            homeViewModel.updateAstronomy()
            navigator().goBack()
        }

        manageLocationAdapter.tempUnit = homeViewModel.getTempUnit()
        manageLocationAdapter.update(viewModel.getLocations())

        val lm = LinearLayoutManager(requireContext())
        lm.orientation = RecyclerView.VERTICAL

        with(binding.locationsList) {
            adapter = manageLocationAdapter
            layoutManager = lm
        }

        homeViewModel.locationsWeatherInfo.observe(viewLifecycleOwner) {
            manageLocationAdapter.updateLocationsInfo(it)
        }
    }

    private fun setupToolbar() {
        navigator().setToolbarTitle(requireContext().getString(R.string.manage_locations))
        navigator().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_arrow_back,
                onAction = { navigator().goBack() }
            )
        )

        navigator().setToolbarRightAction(
            ToolbarAction(
                iconRes = R.drawable.ic_search,
                onAction = { navigator().goToAddLocation() }
            )
        )
    }
}