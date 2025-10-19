package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig

val localToLocalNoBackupTaskConfig: TaskConfig
    get() = LocalToLocalSyncWithoutBackupTaskConfig()

val localToLocalWithBackupTaskConfig: TaskConfig
    get() = LocalToLocalSyncWithBackupTaskConfig(localToLocalNoBackupTaskConfig)