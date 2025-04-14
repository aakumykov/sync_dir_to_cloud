package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.AUTH_ID
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.SOURCE_PATH
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.STORAGE_TYPE
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.TARGET_PATH
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig.TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode

private fun syncTaskWithMode(syncMode: SyncMode): SyncTask = SyncTask(
    sourcePath = SOURCE_PATH,
    targetPath = TARGET_PATH,
    sourceStorageType = STORAGE_TYPE,
    targetStorageType = STORAGE_TYPE,
    syncMode = syncMode,
    intervalHours = 0,
    intervalMinutes = 0,
).apply {
    id = TASK_ID
    this.sourceAuthId = AUTH_ID
    this.targetAuthId = AUTH_ID
}

val TASK_SYNC: SyncTask = syncTaskWithMode(SyncMode.SYNC)
val TASK_MIRROR: SyncTask = syncTaskWithMode(SyncMode.MIRROR)