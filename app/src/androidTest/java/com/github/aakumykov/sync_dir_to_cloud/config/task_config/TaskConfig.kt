package com.github.aakumykov.sync_dir_to_cloud.config.task_config

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode

interface TaskConfig {
    val TASK_ID: String
    val STORAGE_TYPE: StorageType
    val SOURCE_PATH: String
    val TARGET_PATH: String
    val INTERVAL_HOURS: Int
    val INTERVAL_MINUTES: Int
    val SYNC_MODE: SyncMode

    val AUTH_ID: String
    val AUTH_NAME: String
    val AUTH_TOKEN: String
}