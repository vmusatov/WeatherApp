package com.example.weatherapp.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.utils.ToolbarAction
import com.example.weatherapp.ui.utils.ToolbarManager

class MainActivity : AppCompatActivity(), ToolbarManager {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mainToolbar.title = ""

        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
    }

    override fun clearToolbar() {
        binding.toolbarIcon.visibility = View.GONE
        binding.toolbarRightIcon.visibility = View.GONE
        binding.toolbarTitle.visibility = View.GONE
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
}