package com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions

import android.content.Context
import android.widget.Toast

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}