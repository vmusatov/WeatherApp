package com.example.weatherapp.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAboutBinding
import com.example.weatherapp.ui.ToolbarAction
import com.example.weatherapp.ui.toolbarManager

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding: FragmentAboutBinding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        setupToolbar()

        binding.title.text = getString(R.string.app_name)
        binding.version.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        toolbarManager().clearToolbar()
        toolbarManager().setToolbarTitle(requireContext().getString(R.string.about))
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
                iconRes = R.drawable.ic_info,
                onAction = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + requireContext().packageName)
                    startActivity(intent)
                }
            )
        )
    }
}