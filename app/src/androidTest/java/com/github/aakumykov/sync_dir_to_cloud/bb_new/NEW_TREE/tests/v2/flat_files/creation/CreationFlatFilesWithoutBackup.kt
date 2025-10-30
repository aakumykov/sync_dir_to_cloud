package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_files.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Test

open class CreationFlatFilesWithoutBackup : CreationFlatFilesBase() {

    override val taskConfig: TaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun no_files_in_source_and_target() {
        super.no_files_in_source_and_target()
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }


    @Test
    override fun file_created_in_source() {
        super.file_created_in_source()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)
    }


    @Test
    override fun file_created_in_target() {
        super.file_created_in_target()

        assertSourceDirChildCount(0)

        assertTargetDirChildCount(1)
        assertExistsAndContains(tFile, tFileData)
    }


    @Test
    override fun files_created_in_source_and_target() {
        super.files_created_in_source_and_target()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)

        assertTargetDirChildCount(2)
        assertExistsAndContains(sFileInTarget, sFileData)
        assertExistsAndContains(tFile, tFileData)
    }
}