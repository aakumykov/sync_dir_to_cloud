package com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.showToast(text: String) {
    requireContext().showToast(text)
}

fun Fragment.showToast(@StringRes stringRes: Int) {
    requireContext().showToast(stringRes)
}