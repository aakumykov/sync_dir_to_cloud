package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.empty_endpoints

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Test

open class EmptyWithoutBackups : EmptyBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    @Test
    override fun empty_source_and_target_sync() {
        super.empty_source_and_target_sync()
    }
}