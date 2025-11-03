package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.flat_files.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Test

class DeletionFlatFilesWithoutBackup : DeletionFlatFilesBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun source_file_was_deleted() {
        super.source_file_was_deleted()
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }


    @Test
    override fun target_file_was_deleted() {
        super.target_file_was_deleted()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    @Test
    override fun source_and_target_files_are_deleted() {
        super.source_and_target_files_are_deleted()

        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }
}