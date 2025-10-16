package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.adding_new_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Test

class AddingNewDirsWithBackup : AddingNewDirsWithoutBackup() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(super.taskConfig)

    @Test
    override fun preparation_method_test() {
        super.preparation_method_test()
    }

    @Test
    override fun create_additional_dir_in_source() {
        super.create_additional_dir_in_source()
    }

    @Test
    override fun create_additional_dir_in_target() {
        super.create_additional_dir_in_target()
    }

    @Test
    override fun create_diff_names_additional_dirs_in_source_and_target() {
        super.create_diff_names_additional_dirs_in_source_and_target()
    }

    @Test
    override fun create_same_name_additional_dirs_in_source_and_target() {
        super.create_same_name_additional_dirs_in_source_and_target()
    }
}