package com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment

@Deprecated("Перейти на Context.showToast()")
fun Activity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

@Deprecated("Перейти на Context.showToast()")
fun Activity.showToast(@StringRes strRes: Int) {
    showToast(getString(strRes))
}

@Deprecated("Перейти на Context.showToast()")
fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

@Deprecated("Перейти на Context.showToast()")
fun Fragment.showToast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}