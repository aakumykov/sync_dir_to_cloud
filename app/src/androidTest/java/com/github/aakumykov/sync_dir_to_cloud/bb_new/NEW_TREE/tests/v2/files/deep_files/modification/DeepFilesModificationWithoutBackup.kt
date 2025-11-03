package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Test

class DeepFilesModificationWithoutBackup : DeepFilesModificationBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun source_deep_file_changed_by_time() {
        super.source_deep_file_changed_by_time()
        checkSourceAndTargetDeepFiles(newSourceFileData)
    }


    @Test
    override fun source_deep_file_changed_by_size() {
        super.source_deep_file_changed_by_size()
        checkSourceAndTargetDeepFiles(newBigSourceFileData)
    }


    @Test
    override fun target_deep_file_changed_by_time() {
        super.target_deep_file_changed_by_time()
        checkSourceAndTargetDeepFiles(sFileData)
    }


    @Test
    override fun deep_file_in_target_changed_by_size() {
        super.deep_file_in_target_changed_by_size()
        checkSourceAndTargetDeepFiles(sFileData)
    }


    @Test
    override fun both_deep_files_are_changed_by_time() {
        super.both_deep_files_are_changed_by_time()
        checkSourceAndTargetDeepFiles(newSourceFileData)
    }


    @Test
    override fun both_deep_files_are_changed_by_size() {
        super.both_deep_files_are_changed_by_size()
        checkSourceAndTargetDeepFiles(newBigSourceFileData)
    }


    @Test
    override fun file_changed_by_time_in_source_and_by_size_in_target() {
        super.file_changed_by_time_in_source_and_by_size_in_target()
        checkSourceAndTargetDeepFiles(newSourceFileData)
    }


    @Test
    override fun file_changed_by_size_in_source_and_in_target_by_time() {
        super.file_changed_by_size_in_source_and_in_target_by_time()
        checkSourceAndTargetDeepFiles(newBigSourceFileData)
    }


    private fun checkSourceAndTargetDeepFiles(fileData: ByteArray) {
        assertSourceDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, fileData)

        assertTargetDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, fileData)
    }
}