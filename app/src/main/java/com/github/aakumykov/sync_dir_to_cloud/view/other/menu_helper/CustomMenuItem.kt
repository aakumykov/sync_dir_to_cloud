package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

// TODO: переименовать в CustomMenuItem
class CustomMenuItem (
    @IdRes val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val action: Runnable,
    val alwaysVisible: Boolean = true,
    val childItems: Array<CustomMenuItem>? = null
) {
    override fun toString(): String = "CustomMenuItem{${title}}"
}


class CustomActionUpdate(
    @IdRes val id: Int,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int,
    val clickAction: Runnable
)