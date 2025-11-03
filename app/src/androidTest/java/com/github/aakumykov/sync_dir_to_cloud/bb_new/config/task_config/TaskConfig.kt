package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs.defaultLocalSourceDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs.defaultLocalTargetDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.sync_task_with_mode.syncTaskWithMode
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import java.io.File

abstract class TaskConfig(
    open val SOURCE_DIR: File = defaultLocalSourceDir,
    open val TARGET_DIR: File = defaultLocalTargetDir,

    open val SKIP_CREATING_SOURCE_DIR: Boolean = false,
    open val SKIP_CREATING_TARGET_DIR: Boolean = false,

    open val SYNC_MODE: SyncMode = SyncMode.SYNC,

    open val TASK_ID: String = "taskId1",

    open val SOURCE_STORAGE_TYPE: StorageType = StorageType.LOCAL,
    open val TARGET_STORAGE_TYPE: StorageType = StorageType.LOCAL,

    open val INTERVAL_HOURS: Int = 0,
    open val INTERVAL_MINUTES: Int = 0,

    open val SOURCE_PATH: String = SOURCE_DIR.absolutePath,
    open val TARGET_PATH: String = TARGET_DIR.absolutePath,

    open val WITH_BACKUP: Boolean = false,

    open val SOURCE_AUTH_ID: String = "authId1",
    open val TARGET_AUTH_ID: String = "authId1",

    open val TARGET_AUTH_NAME: String = "test_auth_local",
    open val SOURCE_AUTH_NAME: String = "test_auth_local",

    open val SOURCE_AUTH_TOKEN: String = "test_auth_token",
    open val TARGET_AUTH_TOKEN: String = "test_auth_token",

    open val SOURCE_AUTH: CloudAuth = CloudAuth(
        id = SOURCE_AUTH_ID,
        name = SOURCE_AUTH_NAME,
        authToken = SOURCE_AUTH_TOKEN,
        storageType = SOURCE_STORAGE_TYPE
    ),

    open val TARGET_AUTH: CloudAuth = SOURCE_AUTH,
) {
    val TASK_SYNC: SyncTask
        get() = syncTaskWithMode(SyncMode.SYNC, this)

    val TASK_MIRROR: SyncTask
        get() = syncTaskWithMode(SyncMode.MIRROR, this)
}