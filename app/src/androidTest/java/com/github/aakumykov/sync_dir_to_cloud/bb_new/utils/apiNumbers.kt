package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import android.os.Build
import android.os.Environment
import java.io.File

val apiNumber: Int
    get() = Build.VERSION.SDK_INT


val apiDirInDownloads: File
    get() = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        apiNumber.toString()
    )