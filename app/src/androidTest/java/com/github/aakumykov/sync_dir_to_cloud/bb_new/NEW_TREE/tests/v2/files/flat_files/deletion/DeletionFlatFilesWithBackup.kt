package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.flat_files.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class DeletionFlatFilesWithBackup : DeletionFlatFilesBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig

    @Test
    override fun source_file_was_deleted() {
        super.source_file_was_deleted()
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(1)
        assertOnlyFileWasBackupedInTarget(sFileName, sFileData)
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