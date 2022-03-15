package com.example.weatherapp.ui.locations

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAddLocationBinding
import com.example.weatherapp.ui.*
import com.example.weatherapp.ui.home.HomeViewModel


class AddLocationFragment : Fragment() {

    private lateinit var binding: FragmentAddLocationBinding
    private lateinit var manageLocationAdapter: AddLocationListAdapter

    private val viewModel: AddLocationViewModel by viewModels { viewModelFactory() }
    private val homeViewModel: HomeViewModel by activityViewModels { viewModelFactory() }

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val q = s.toString()
            if (q.isEmpty()) {
                binding.notFound.visibility = View.GONE
                binding.locationsList.visibility = View.GONE
            } else {
                viewModel.search(q.trim())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLocationBinding.inflate(inflater, container, false)

        setupUi()
        setupObservers()

        return binding.root
    }

    private fun setupUi() {
        setupToolbar()

        manageLocationAdapter = AddLocationListAdapter {
            viewModel.saveLocation(it)
            homeViewModel.updateLocationsWeatherInfo()
            navigator().goBack()
        }

        val lm = LinearLayoutManager(requireContext())
        lm.orientation = RecyclerView.VERTICAL

        with(binding.locationsList) {
            adapter = manageLocationAdapter
            layoutManager = lm
        }

        binding.location.addTextChangedListener(searchTextWatcher)
        binding.location.requestFocus()

    }

    private fun setupToolbar() {
        navigator().setToolbarTitle(requireContext().getString(R.string.add_location))
        navigator().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_arrow_back,
                onAction = { navigator().goBack() }
            )
        )
    }

    private fun setupObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                binding.locationsList.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            } else {
                val result = if (it.size > 10) it.subList(0, 10) else it
                manageLocationAdapter.update(result)
                binding.locationsList.visibility = View.VISIBLE
                binding.notFound.visibility = View.GONE
            }
        }
    }
}