package com.github.aakumykov.sync_dir_to_cloud.config.task_config

import android.os.Build
import android.os.Environment
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import java.io.File

object LocalTaskConfig : TaskConfig {

    override val TASK_ID = "taskId1"
    override val STORAGE_TYPE = StorageType.LOCAL
    override val SOURCE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    override val TARGET_PATH: String = File(
        Environment.getExternalStorageDirectory(),
        "d${Build.VERSION.SDK_INT}"
    ).absolutePath
    override val SYNC_MODE: SyncMode = SyncMode.SYNC
    override val INTERVAL_HOURS = 0
    override val INTERVAL_MINUTES = 0

    override val AUTH_ID = "authId1"
    override val AUTH_NAME = "test_auth_local"
    override val AUTH_TOKEN = "test_auth_token"
}
