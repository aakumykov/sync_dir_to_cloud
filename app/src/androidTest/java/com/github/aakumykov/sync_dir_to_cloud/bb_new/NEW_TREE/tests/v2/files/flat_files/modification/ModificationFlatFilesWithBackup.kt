package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class ModificationFlatFilesWithBackup(numberOfRun: Int) : ModificationFlatFilesBase(numberOfRun) {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig


    @Test
    override fun empty_test_() {
        super.empty_test_()
    }


    @Test
    override fun source_file_was_changed_by_size() {
        super.source_file_was_changed_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newBigSourceFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, newBigSourceFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, sFileData)
    }


    @Test
    override fun source_file_was_changed_by_time() {
        super.source_file_was_changed_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, newSourceFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, sFileData)
    }


    @Test
    override fun target_file_was_changed_by_size() {
        super.target_file_was_changed_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, sFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, newBigTargetFileData)
    }


    @Test
    override fun target_file_was_changed_by_time() {
        super.target_file_was_changed_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, sFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, newTargetFileData)
    }


    @Test
    override fun source_and_target_files_was_changed_by_time() {
        super.source_and_target_files_was_changed_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, newSourceFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, newTargetFileData)
    }


    @Test
    override fun source_and_target_files_was_changed_by_size() {
        super.source_and_target_files_was_changed_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newBigSourceFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, newBigSourceFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, newBigTargetFileData)
    }


    @Test
    override fun source_file_was_changed_by_time_and_target_by_size() {
        super.source_file_was_changed_by_time_and_target_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, newSourceFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, newBigTargetFileData)
    }


    @Test
    override fun source_file_was_changed_by_size_and_target_by_time() {
        super.source_file_was_changed_by_size_and_target_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newBigSourceFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, newBigSourceFileData)

        assertOnlyFileWasBackupedInTarget(sFileName, newTargetFileData)
    }
}