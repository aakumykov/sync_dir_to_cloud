package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode

fun syncTaskWithMode(
    syncMode: SyncMode,
    taskConfig: TaskConfig,
): SyncTask = SyncTask(
    sourcePath = taskConfig.SOURCE_PATH,
    targetPath = taskConfig.TARGET_PATH,
    sourceStorageType = taskConfig.STORAGE_TYPE,
    targetStorageType = taskConfig.STORAGE_TYPE,
    syncMode = syncMode,
    intervalHours = taskConfig.INTERVAL_HOURS,
    intervalMinutes = taskConfig.INTERVAL_MINUTES,
).apply {
    id = taskConfig.TASK_ID
    sourceAuthId = taskConfig.SOURCE_AUTH_ID
    targetAuthId = taskConfig.TARGET_AUTH_ID
}
