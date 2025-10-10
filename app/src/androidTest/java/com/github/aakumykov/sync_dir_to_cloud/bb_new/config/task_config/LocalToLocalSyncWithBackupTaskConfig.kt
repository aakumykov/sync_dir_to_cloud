package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

class LocalToLocalSyncWithBackupTaskConfig(
    private val parentTaskConfig: TaskConfig
): TaskConfig by parentTaskConfig {
    override val WITH_BACKUP: Boolean = true
}