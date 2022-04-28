package com.example.weatherapp.ui

import androidx.annotation.DrawableRes

class ToolbarAction(
    @DrawableRes val iconRes: Int,
    val onAction: Runnable
)

interface ToolbarManager {

    fun clearToolbar()

    fun setToolbarTitle(title: String)

    fun setToolbarAction(action: ToolbarAction)

    fun setToolbarRightAction(action: ToolbarAction)
}