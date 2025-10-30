package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Test

class CreatingFlatDirsWithBackup : CreatingFlatDirsWithoutBackup() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(super.taskConfig)


    // ==== sync ===>
    @Test
    override fun only_in_source_dir() {
        super.only_in_source_dir()
    }


    // ==== sync ===>
    @Test
    override fun only_in_target_dir() {
        super.only_in_target_dir()
    }


    // ==== sync ===>
    @Test
    override fun same_name_dirs_in_source_and_target() {
        super.same_name_dirs_in_source_and_target()
    }


    // ==== sync ===>
    @Test
    override fun diff_name_dirs_in_source_and_target() {
        super.diff_name_dirs_in_source_and_target()
    }
}