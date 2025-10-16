package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Test

open class SourceAndTargetDirsWithBackup : SourceAndTargetDirsWithoutBackup() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(super.taskConfig)

    @Test
    override fun empty_source_and_target_dirs_exists() {
        super.empty_source_and_target_dirs_exists()
    }
}