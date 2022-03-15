package com.example.weatherapp.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.home.HomeFragment
import com.example.weatherapp.ui.locations.AddLocationFragment
import com.example.weatherapp.ui.locations.ManageLocationsFragment
import com.example.weatherapp.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mainToolbar.title = ""

        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, HomeFragment())
                .commit()
        }
    }

    override fun openSettings() {
        launchFragmentFromRight(SettingsFragment())
    }

    override fun goBack() {
        clearToolbar()
        onBackPressed()
    }

    override fun goToManageLocations() {
        launchFragmentFromLeft(ManageLocationsFragment())
    }

    override fun goToAddLocation() {
        launchFragment(AddLocationFragment())
    }

    override fun setToolbarTitle(title: String) {
        binding.toolbarTitle.visibility = View.VISIBLE
        binding.toolbarTitle.text = title
    }

    override fun setToolbarAction(action: ToolbarAction) {
        updateToolbarAction(binding.toolbarIcon, action)
    }

    override fun setToolbarRightAction(action: ToolbarAction) {
        updateToolbarAction(binding.toolbarRightIcon, action)
    }

    private fun updateToolbarAction(image: ImageView, action: ToolbarAction) {
        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        image.visibility = View.VISIBLE
        image.setImageDrawable(iconDrawable)
        image.setOnClickListener {
            action.onAction.run()
        }
    }

    private fun clearToolbar() {
        binding.toolbarIcon.visibility = View.GONE
        binding.toolbarRightIcon.visibility = View.GONE
        binding.toolbarTitle.visibility = View.GONE
    }

    private fun launchFragment(fragment: Fragment, anim: FragmentAnimation? = null) {
        clearToolbar()
        val transaction = supportFragmentManager.beginTransaction()

        if (anim != null) {
            transaction.setCustomAnimations(anim.enter, anim.exit, anim.popEnter, anim.popExit)
        }

        transaction
            .addToBackStack(null)
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun launchFragmentFromLeft(fragment: Fragment) {
        launchFragment(fragment, FragmentAnimation(
            R.anim.enter_from_left,
            R.anim.exit_to_right,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        ))
    }

    private fun launchFragmentFromRight(fragment: Fragment) {
        launchFragment(fragment, FragmentAnimation(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        ))
    }
}