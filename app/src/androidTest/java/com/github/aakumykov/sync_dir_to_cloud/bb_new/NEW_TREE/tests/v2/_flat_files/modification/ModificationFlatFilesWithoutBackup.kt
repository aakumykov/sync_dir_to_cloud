package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Test

class ModificationFlatFilesWithoutBackup(numberOfRun: Int) : ModificationFlatFilesBase(numberOfRun) {

    override val taskConfig: TaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun empty_test_() {
        super.empty_test_()
    }


    @Test
    override fun source_file_was_changed_by_size() {
        super.source_file_was_changed_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newBigSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newBigSourceFileData)
    }


    @Test
    override fun source_file_was_changed_by_time() {
        super.source_file_was_changed_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newSourceFileData)
    }


    @Test
    override fun target_file_was_changed_by_size() {
        super.target_file_was_changed_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    @Test
    override fun target_file_was_changed_by_time() {
        super.target_file_was_changed_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    @Test
    override fun source_and_target_files_was_changed_by_time() {
        super.source_and_target_files_was_changed_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newSourceFileData)
    }


    @Test
    override fun source_and_target_files_was_changed_by_size() {
        super.source_and_target_files_was_changed_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newBigSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newBigSourceFileData)
    }


    @Test
    override fun source_file_was_changed_by_time_and_target_by_size() {
        super.source_file_was_changed_by_time_and_target_by_size()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newSourceFileData)
    }


    @Test
    override fun source_file_was_changed_by_size_and_target_by_time() {
        super.source_file_was_changed_by_size_and_target_by_time()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newBigSourceFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newBigSourceFileData)
    }
}