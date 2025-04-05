package com.github.aakumykov.sync_dir_to_cloud.config

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType

interface TaskConfig {
    val TASK_ID: String
    val STORAGE_TYPE: StorageType
    val SOURCE_PATH: String
    val TARGET_PATH: String
    val AUTH_ID: String
    val AUTH_NAME: String
    val AUTH_TOKEN: String
}