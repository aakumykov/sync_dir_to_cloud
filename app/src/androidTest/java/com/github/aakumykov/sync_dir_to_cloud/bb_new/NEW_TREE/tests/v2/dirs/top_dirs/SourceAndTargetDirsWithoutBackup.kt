package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Assert
import org.junit.Test

open class SourceAndTargetDirsWithoutBackup : SourceAndTargetDirsBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun empty_source_and_target_dirs_exists() {
        super.empty_source_and_target_dirs_exists()
    }

    @Test
    override fun re_creating_missing_source_dir() {
        super.re_creating_missing_source_dir()
        Assert.assertTrue(taskConfig.SOURCE_DIR.exists())
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
    }

    @Test
    override fun re_creating_missing_target_dir() {
        super.re_creating_missing_target_dir()
        Assert.assertTrue(taskConfig.TARGET_DIR.exists())
        Assert.assertTrue(taskConfig.TARGET_DIR.isEmpty)
    }
}