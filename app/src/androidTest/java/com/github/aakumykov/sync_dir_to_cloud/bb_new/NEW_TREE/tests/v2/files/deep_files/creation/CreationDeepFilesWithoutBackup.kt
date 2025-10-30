package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Test

class CreationDeepFilesWithoutBackup : CreationDeepFilesBase() {

    override val taskConfig: TaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun deep_file_created_in_source() {
        super.deep_file_created_in_source()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sDeepFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sDeepFileInTarget, sFileData)
    }


    @Test
    override fun deep_file_created_in_target() {
        super.deep_file_created_in_target()

        assertSourceDirChildCount(0)

        assertTargetDirChildCount(1)
        assertExistsAndContains(tDeepFile, tFileData)
    }


    @Test
    override fun same_name_same_content_deep_files_created_in_source_and_target() {
        super.same_name_same_content_deep_files_created_in_source_and_target()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sDeepFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sDeepFileInTarget, sFileData)
    }


    @Test
    override fun same_name_diff_content_deep_files_created_in_source_and_target() {
        super.same_name_diff_content_deep_files_created_in_source_and_target()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sDeepFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sDeepFileInTarget, sFileData)
    }


    @Test
    override fun diff_name_deep_files_created_in_source_and_target() {
        super.diff_name_deep_files_created_in_source_and_target()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sDeepFile, sFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sDeepFileInTarget, sFileData)
        assertExistsAndContains(tDeepFile, tFileData)
    }
}