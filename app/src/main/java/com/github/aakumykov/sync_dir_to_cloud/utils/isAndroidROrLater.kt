package com.github.aakumykov.sync_dir_to_cloud.utils

import android.os.Build

fun isAndroidROrLater() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun isAndroidTiramisuOrLater() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU