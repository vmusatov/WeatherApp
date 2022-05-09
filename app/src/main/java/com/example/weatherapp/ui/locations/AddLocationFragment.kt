package com.example.weatherapp.ui.locations

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentAddLocationBinding
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.ui.*
import com.example.weatherapp.ui.locations.adapter.AddLocationListAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddLocationFragment : Fragment() {

    private var _binding: FragmentAddLocationBinding? = null
    private val binding: FragmentAddLocationBinding get() = checkNotNull(_binding)

    private lateinit var manageLocationAdapter: AddLocationListAdapter

    @Inject
    lateinit var addLocationsFactory: AddLocationViewModel.Factory
    private val viewModel: AddLocationViewModel by viewModels { addLocationsFactory }

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val q = s.toString().trim()
            if (q.isEmpty()) {
                binding.errorText.visibility = View.GONE
                binding.locationsList.visibility = View.GONE
            } else {
                viewModel.search(q)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val editorActionListener = TextView.OnEditorActionListener { textView, actionId, _ ->
        val text = textView.text.toString().trim()
        if (text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {
            viewModel.search(text)
            hideSoftKeyboard(binding.locationSearchText)
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manageLocationAdapter = AddLocationListAdapter { addLocation(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)

        setupUi()
        setupObservers()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSoftKeyboard(binding.locationSearchText)
    }

    private fun addLocation(location: Location) {
        if (viewModel.isLocationExist(location)) {
            showShortToast(getString(R.string.location_alredy_added))
        } else {
            lifecycleScope.launch {
                viewModel.saveLocation(location).join()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupUi() = with(binding) {
        setupToolbar()

        locationsList.adapter = manageLocationAdapter
        locationsList.layoutManager = LinearLayoutManager(requireContext())

        locationSearchText.addTextChangedListener(searchTextWatcher)
        locationSearchText.setOnEditorActionListener(editorActionListener)

        map.setOnClickListener {
            findNavController().navigate(R.id.action_addLocationFragment_to_mapFragment)
        }
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

    private fun setupObservers() = with(binding) {
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                locationsList.visibility = View.GONE
                errorText.text = getString(R.string.nothing_found)
                errorText.visibility = View.VISIBLE
            } else {
                val result = if (it.size > 10) it.subList(0, 10) else it
                manageLocationAdapter.update(result)
                locationsList.scrollToPosition(0)
                locationsList.visibility = View.VISIBLE
                errorText.visibility = View.GONE
                locationsList.requestApplyInsets()
            }
        }

        viewModel.updateFail.observe(viewLifecycleOwner) {
            locationsList.visibility = View.GONE
            errorText.visibility = View.VISIBLE

            when (it) {
                UpdateFailType.FAIL_LOAD_FROM_NETWORK -> {
                    errorText.text = getString(R.string.network_fail)
                }
                else -> {
                    errorText.text = getString(R.string.undefined_fail)
                }
            }
        }
    }
}