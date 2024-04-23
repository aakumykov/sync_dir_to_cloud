package com.github.aakumykov.sync_dir_to_cloud.view.view_utils

import android.view.View

fun View.hideIf(condition: View.() -> Boolean) {
    visibility = if (condition()) View.GONE else View.VISIBLE
}