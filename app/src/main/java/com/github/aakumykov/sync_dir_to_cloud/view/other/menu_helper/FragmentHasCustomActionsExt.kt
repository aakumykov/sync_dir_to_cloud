package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.fragment.app.Fragment

fun Fragment.getCustomActions(): Array<CustomMenuAction>? {
    return if (this is HasCustomActions) this.getCustomActions()
    else null
}