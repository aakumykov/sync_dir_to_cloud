package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.deep_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class StaticDeepDirsWithBackup : StaticDeepDirsWithoutBackup() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig


    @Test
    override fun deep_empty_dir_in_source() {
        super.deep_empty_dir_in_source()
    }

    @Test
    override fun deep_empty_dir_in_target() {
        super.deep_empty_dir_in_target()
    }

    @Test
    override fun same_name_deep_empty_dirs_in_source_and_target() {
        super.same_name_deep_empty_dirs_in_source_and_target()
    }

    @Test
    override fun diff_name_deep_dirs_in_source_and_target() {
        super.diff_name_deep_dirs_in_source_and_target()
    }
}