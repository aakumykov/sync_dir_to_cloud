package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

// TODO: переименовать в CustomMenuItem
class CustomMenuAction(
    @IdRes val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val alwaysVisible: Boolean = true,
    val clickAction: Runnable,
    val longClickAction: Runnable? = null, // TODO: убрать
    val childItems: Array<CustomMenuAction>? = null
) {
    override fun toString(): String = "CustomMenuAction{${title}}"
}


class CustomActionUpdate(
    @IdRes val id: Int,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int,
    val clickAction: Runnable
)