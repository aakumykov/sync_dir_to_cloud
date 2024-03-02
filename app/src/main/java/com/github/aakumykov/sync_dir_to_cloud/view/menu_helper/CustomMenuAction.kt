package com.github.aakumykov.sync_dir_to_cloud.view.menu_helper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class CustomMenuAction(
    val itemId: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val alwaysVisible: Boolean,
    val clickRunnable: Runnable,
    val longClickRunnable: Runnable? = null,
    val childActions: Array<CustomMenuAction>? = null
)
