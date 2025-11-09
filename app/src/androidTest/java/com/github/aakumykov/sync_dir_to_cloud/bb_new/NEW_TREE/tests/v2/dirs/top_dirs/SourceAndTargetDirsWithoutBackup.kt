package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Assert
import org.junit.Test

open class SourceAndTargetDirsWithoutBackup : SourceAndTargetDirsBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun empty_source_and_target_dirs_exists_at_start() {
        super.empty_source_and_target_dirs_exists_at_start()
    }


    @Test
    override fun re_creating_missing_source_dir_if_configured_yes() {
        super.re_creating_missing_source_dir_if_configured_yes()
        assertExistsAndEmpty(taskConfig.SOURCE_DIR)
    }


    @Test
    override fun re_creating_missing_target_dir_if_configured_yes() {
        super.re_creating_missing_target_dir_if_configured_yes()
        assertExistsAndEmpty(taskConfig.TARGET_DIR)
    }


    @Test
    override fun re_creating_missing_source_dir_if_configured_no() {
        super.re_creating_missing_source_dir_if_configured_no()
        Assert.assertFalse(taskConfig.SOURCE_DIR.exists())
    }


    @Test
    override fun re_creating_missing_target_dir_if_configured_no() {
        super.re_creating_missing_target_dir_if_configured_no()
        Assert.assertFalse(taskConfig.TARGET_DIR.exists())
    }
}