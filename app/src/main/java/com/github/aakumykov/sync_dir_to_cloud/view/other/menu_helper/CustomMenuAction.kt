package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

class CustomMenuAction(
    @IdRes val itemId: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val alwaysVisible: Boolean = true,
    val clickAction: Runnable,
    val longClickAction: Runnable? = null,
    val childItems: Array<CustomMenuAction>? = null
)
