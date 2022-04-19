package com.example.weatherapp.ui.settings

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.appComponent
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.domain.model.TempUnit
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.navigator
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SettingsFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() =  checkNotNull(_binding)

    @Inject
    lateinit var factory: SettingsViewModel.Factory
    private val viewModel: SettingsViewModel by viewModels { factory }

    private lateinit var selectedUnit: TextView
    private lateinit var tempUnitBlock: LinearLayout
    private lateinit var about: TextView

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupFields()
        setupObservers()
        setupUi()
        setupToolbar()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.tempUnit.observe(viewLifecycleOwner) { selectedUnit.text = it.unitName }
    }

    private fun setupFields() {
        selectedUnit = binding.selectedUnit
        tempUnitBlock = binding.tempUnitBlock
        about = binding.about
    }

    private fun setupUi() {
        tempUnitBlock.setOnClickListener {
            showPopup(
                binding.unitTitle,
                R.menu.change_temp_unit_action,
                runBlocking { viewModel.getTempUnit().unitName }
            )
        }
        about.setOnClickListener { navigator().goToAbout() }
    }

    private fun showPopup(view: View, @MenuRes menuRes: Int, selectedItem: String? = null) {
        val popup = PopupMenu(requireContext(), view)
        val inflater = popup.menuInflater
        inflater.inflate(menuRes, popup.menu)

        if (selectedItem != null) {
            popup.menu.iterator().forEach {
                if (it.title == selectedItem) {
                    val spannable = SpannableString(it.title)
                    val selectedColor =
                        ForegroundColorSpan(getColor(requireContext(), R.color.selected))

                    spannable.setSpan(selectedColor, 0, spannable.length, 0)
                    it.title = spannable

                    return@forEach
                }
            }
        }

        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.C -> {
                updateTempUnit(TempUnit.C)
                true
            }
            R.id.F -> {
                updateTempUnit(TempUnit.F)
                true
            }
            else -> false
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

    private fun updateTempUnit(tempUnit: TempUnit) {
        viewModel.saveTempUnit(tempUnit)
    }
}