package com.github.aakumykov.sync_dir_to_cloud.utils

import android.os.Build

fun isEmulator(): Boolean = Build.HARDWARE.equals("ranchu")