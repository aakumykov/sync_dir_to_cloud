package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class CreationDeepFilesWithBackup : CreationDeepFilesBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig


    @Test
    override fun deep_file_created_in_source() {
        super.deep_file_created_in_source()
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, sFileData)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, sFileData)
    }


    @Test
    override fun deep_file_created_in_target() {
        super.deep_file_created_in_target()
        assertSourceDirChildCount(0)
        assertOnlyDeepFileExistsAndContainsInTarget(tDeepDirName, tFileName, tFileData)
    }


    // FIXME: спорная ситуация!
    @Test
    override fun same_name_same_content_deep_files_created_in_source_and_target() {
        super.same_name_same_content_deep_files_created_in_source_and_target()

        assertSourceDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, sFileData)

        assertTargetDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, sFileData)
//        assertExecutionBackupDirInSourceChildCount(1)
//        assertTargetExecutionBackupDirExistsAndContains(sFileName, sFileData)
    }


    @Test
    override fun same_name_diff_content_deep_files_created_in_source_and_target() {
        super.same_name_diff_content_deep_files_created_in_source_and_target()

        val sList = taskConfig.SOURCE_DIR.list()
        val tList = taskConfig.TARGET_DIR.list()

        assertSourceDirChildCount(1)
        assertTargetDirChildCount(2)
    }


    @Test
    override fun diff_name_deep_files_created_in_source_and_target() {
        super.diff_name_deep_files_created_in_source_and_target()

        val sList = taskConfig.SOURCE_DIR.list()
        val tList = taskConfig.TARGET_DIR.list()

        assertSourceDirChildCount(1)
        assertTargetDirChildCount(2)
    }
}