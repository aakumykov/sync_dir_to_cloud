package com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions

import android.app.Activity
import androidx.annotation.StringRes

fun Activity.showToast(text: String) {
    applicationContext.showToast(text)
}

fun Activity.showToast(@StringRes strRes: Int) {
    applicationContext.showToast(getString(strRes))
}