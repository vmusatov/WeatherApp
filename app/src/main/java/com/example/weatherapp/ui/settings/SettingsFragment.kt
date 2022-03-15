package com.example.weatherapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.ui.*
import com.example.weatherapp.ui.home.HomeViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: HomeViewModel by activityViewModels { viewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupUi()
        setupToolbar()

        return binding.root
    }

    private fun setupUi() {
        binding.unit.text = viewModel.getTempUnit().unitName

        binding.unitTitle.setOnClickListener {
            val newTempUnit = viewModel.getTempUnit().invert()

            binding.unit.text = newTempUnit.unitName
            viewModel.saveTempUnit(newTempUnit)
        }
    }

    private fun setupToolbar() {
        navigator().setToolbarTitle(requireContext().getString(R.string.options))
        navigator().setToolbarAction(
            ToolbarAction(
                iconRes = R.drawable.ic_arrow_back,
                onAction = { navigator().goBack() }
            )
        )
    }

    companion object {
        const val PREF_TEMP_CODE = "PREF_TEMP_CODE"
    }
}