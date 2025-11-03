package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs.defaultLocalSourceDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs.defaultLocalTargetDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.sync_task_with_mode.syncTaskWithMode
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import java.io.File

open class LocalToLocalSyncNoBackupTaskConfig(
    override val SOURCE_DIR: File = defaultLocalSourceDir,
    override val TARGET_DIR: File = defaultLocalTargetDir,

    override val SKIP_CREATING_SOURCE_DIR: Boolean = false,
    override val SKIP_CREATING_TARGET_DIR: Boolean = false,

    override val SYNC_MODE: SyncMode = SyncMode.SYNC,

    override val TASK_ID: String = "taskId1",

    override val SOURCE_STORAGE_TYPE: StorageType = StorageType.LOCAL,
    override val TARGET_STORAGE_TYPE: StorageType = StorageType.LOCAL,

    override val INTERVAL_HOURS: Int = 0,
    override val INTERVAL_MINUTES: Int = 0,

    override val SOURCE_PATH: String = SOURCE_DIR.absolutePath,
    override val TARGET_PATH: String = TARGET_DIR.absolutePath,

    override val WITH_BACKUP: Boolean = false,

    override val SOURCE_AUTH_ID: String = "authId1",
    override val TARGET_AUTH_ID: String = "authId1",

    override val TARGET_AUTH_NAME: String = "test_auth_local",
    override val SOURCE_AUTH_NAME: String = "test_auth_local",

    override val SOURCE_AUTH_TOKEN: String = "test_auth_token",
    override val TARGET_AUTH_TOKEN: String = "test_auth_token",

    override val SOURCE_AUTH: CloudAuth = CloudAuth(
        id = SOURCE_AUTH_ID,
        name = SOURCE_AUTH_NAME,
        authToken = SOURCE_AUTH_TOKEN,
        storageType = SOURCE_STORAGE_TYPE
    ),

    override val TARGET_AUTH: CloudAuth = SOURCE_AUTH,
)
    : TaskConfig()