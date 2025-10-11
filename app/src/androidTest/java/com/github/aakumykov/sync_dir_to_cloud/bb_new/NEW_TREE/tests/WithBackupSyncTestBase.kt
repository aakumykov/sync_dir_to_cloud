package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig

abstract class WithBackupSyncTestBase : NoBackupSyncTestBase() {
    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(super.taskConfig)
}