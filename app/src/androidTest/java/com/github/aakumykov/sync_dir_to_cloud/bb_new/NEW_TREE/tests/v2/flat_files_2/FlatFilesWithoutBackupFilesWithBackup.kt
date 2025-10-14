package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_files_2

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig

class FlatFilesWithoutBackupFilesWithBackup : FlatFilesBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(LocalToLocalSyncWithoutBackupTaskConfig())



}
