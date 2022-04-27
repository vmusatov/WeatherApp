package com.example.weatherapp.ui

import androidx.annotation.DrawableRes

class ToolbarAction(
    @DrawableRes val iconRes: Int,
    val onAction: Runnable
)

interface Navigator {
    fun openSettings()

    fun goBack()

    fun goToManageLocations()

    fun goToAddLocation()

    fun goToAbout()

    fun goToMap()

    fun setToolbarTitle(title: String)

    fun setToolbarAction(action: ToolbarAction)

    fun setToolbarRightAction(action: ToolbarAction)
}