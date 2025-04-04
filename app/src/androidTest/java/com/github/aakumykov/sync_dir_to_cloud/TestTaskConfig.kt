package com.github.aakumykov.sync_dir_to_cloud

import android.os.Build
import android.os.Environment
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import java.io.File

object TestTaskConfig {
    const val ID = "taskId1"

    val STORAGE_TYPE = StorageType.LOCAL

    val SOURCE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

    val TARGET_PATH: String = File(
        Environment.getExternalStorageDirectory(),
        "d${Build.VERSION.SDK_INT}"
    ).absolutePath

    const val AUTH_ID = "authId1"
    const val AUTH_NAME = "test_auth_local"
    const val AUTH_TOKEN = "test_auth_token"
}