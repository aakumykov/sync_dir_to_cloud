package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.sync_task_with_mode.syncTaskWithMode
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

class LocalToLocalSyncWithBackupTaskConfig(private val parentTaskConfig: TaskConfig)
    : TaskConfig by parentTaskConfig
{
    override val WITH_BACKUP: Boolean
        get() = true

    override val TASK_SYNC: SyncTask
        get() = syncTaskWithMode(SYNC_MODE, this)

    override val TASK_MIRROR: SyncTask
        get() = syncTaskWithMode(SYNC_MODE, this)
}