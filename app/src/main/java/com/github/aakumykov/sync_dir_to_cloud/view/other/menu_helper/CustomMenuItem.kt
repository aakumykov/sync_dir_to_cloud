package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

class CustomMenuItem (
    @IdRes val id: Int,
    @StringRes var title: Int,
    @DrawableRes var icon: Int,
    var action: Runnable,
    var alwaysVisible: Boolean = true,
    var childItems: Array<CustomMenuItem>? = null
) {
    override fun toString(): String = "CustomMenuItem{${title}}"
}
