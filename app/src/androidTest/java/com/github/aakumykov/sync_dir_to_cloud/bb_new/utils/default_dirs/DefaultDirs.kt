package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs

import android.os.Environment
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.api_number.apiNumber
import java.io.File

val defaultLocalSourceDir: File
    get() = File(
        Environment.getExternalStorageDirectory(),
        "s$apiNumber"
    )
val defaultLocalTargetDir: File
    get() = File(
        Environment.getExternalStorageDirectory(),
        "d$apiNumber"
    )