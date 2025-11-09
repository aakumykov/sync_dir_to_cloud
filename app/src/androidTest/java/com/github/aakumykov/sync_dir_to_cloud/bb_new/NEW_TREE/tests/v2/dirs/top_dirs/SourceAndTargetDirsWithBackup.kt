package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.top_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

open class SourceAndTargetDirsWithBackup : SourceAndTargetDirsWithoutBackup() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig


    @Test
    override fun empty_source_and_target_dirs_exists_at_start() {
        super.empty_source_and_target_dirs_exists_at_start()
    }


    @Test
    override fun re_creating_missing_source_dir_if_configured_yes() {
        super.re_creating_missing_source_dir_if_configured_yes()
    }


    @Test
    override fun re_creating_missing_target_dir_if_configured_yes() {
        super.re_creating_missing_target_dir_if_configured_yes()
    }


    @Test
    override fun re_creating_missing_source_dir_if_configured_no() {
        super.re_creating_missing_source_dir_if_configured_no()
    }


    @Test
    override fun re_creating_missing_target_dir_if_configured_no() {
        super.re_creating_missing_target_dir_if_configured_no()
    }
}