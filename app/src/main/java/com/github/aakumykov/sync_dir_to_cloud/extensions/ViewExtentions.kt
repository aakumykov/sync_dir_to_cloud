package com.github.aakumykov.sync_dir_to_cloud.extensions

import android.view.View
import androidx.annotation.StringRes

fun View.getString(@StringRes stringRes: Int, vararg args: Any): String {
    return resources.getString(stringRes)
}