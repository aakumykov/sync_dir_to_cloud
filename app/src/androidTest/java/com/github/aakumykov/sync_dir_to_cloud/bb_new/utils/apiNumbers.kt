package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import android.os.Build
import android.os.Environment
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import java.io.File

val apiNumber: Int
    get() = Build.VERSION.SDK_INT


val defaultSourceDir: File
    get() = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "s${apiNumber}"
    )

val defaultTargetDir: File
    get() = File(
        Environment.getExternalStorageDirectory(),
        "d${apiNumber}"
    )
