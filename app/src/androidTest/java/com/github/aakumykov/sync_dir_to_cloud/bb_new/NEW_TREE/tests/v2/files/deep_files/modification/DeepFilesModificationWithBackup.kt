package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class DeepFilesModificationWithBackup : DeepFilesModificationBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig


    @Test
    override fun source_deep_file_changed_by_time() {
        super.source_deep_file_changed_by_time()
        checkSourceAndTargetFiles(newSourceFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, sFileData)
    }


    @Test
    override fun source_deep_file_changed_by_size() {
        super.source_deep_file_changed_by_size()
        checkSourceAndTargetFiles(newBigSourceFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, sFileData)
    }


    @Test
    override fun target_deep_file_changed_by_time() {
        super.target_deep_file_changed_by_time()
        checkSourceAndTargetFiles(sFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, newTargetFileData)
    }


    @Test
    override fun deep_file_in_target_changed_by_size() {
        super.deep_file_in_target_changed_by_size()
        checkSourceAndTargetFiles(sFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, newBigTargetFileData)

    }


    @Test
    override fun both_deep_files_are_changed_by_time() {
        super.both_deep_files_are_changed_by_time()
        checkSourceAndTargetFiles(newSourceFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, newTargetFileData)
    }


    @Test
    override fun both_deep_files_are_changed_by_size() {
        super.both_deep_files_are_changed_by_size()
        checkSourceAndTargetFiles(newBigSourceFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, newBigTargetFileData)
    }


    @Test
    override fun file_changed_by_time_in_source_and_by_size_in_target() {
        super.file_changed_by_time_in_source_and_by_size_in_target()
        checkSourceAndTargetFiles(newSourceFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, newBigTargetFileData)
    }


    @Test
    override fun file_changed_by_size_in_source_and_in_target_by_time() {
        super.file_changed_by_size_in_source_and_in_target_by_time()
        checkSourceAndTargetFiles(newBigSourceFileData)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, newTargetFileData)
    }


    private fun checkSourceAndTargetFiles(fileData: ByteArray) {
        assertSourceDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, fileData)

        assertTargetDirChildCount(2)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, fileData)
    }
}