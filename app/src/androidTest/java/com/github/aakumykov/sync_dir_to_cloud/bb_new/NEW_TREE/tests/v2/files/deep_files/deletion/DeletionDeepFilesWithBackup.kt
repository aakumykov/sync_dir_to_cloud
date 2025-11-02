package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_is_empty_as_all_levels.assertDeepDirIsEmptyAtAllLevels
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class DeletionDeepFilesWithBackup : DeletionDeepFilesBase() {

    override val taskConfig: TaskConfig
        get() = localToLocalWithBackupTaskConfig

    @Test
    override fun deep_file_deletion_in_source() {
        super.deep_file_deletion_in_source()

        assertSourceDirChildCount(1)
        assertDeepDirIsEmptyAtAllLevels(taskConfig.SOURCE_DIR, sDeepDirName)

        assertTargetDirChildCount(2)
        assertDeepDirIsEmptyAtAllLevels(taskConfig.TARGET_DIR, sDeepDirName)
        assertTargetExecutionBackupDirContainsOnlyDeepFileAtTheEnd(sDeepDirName, sFileName, sFileData)

    }

    @Test
    override fun deep_file_deletion_in_target() {
        super.deep_file_deletion_in_target()

        assertSourceDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, sFileData)

        assertTargetDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, sFileData)

    }

    @Test
    override fun deep_files_deletion_in_source_and_target() {
        super.deep_files_deletion_in_source_and_target()

        assertSourceDirChildCount(1)
        assertDeepDirIsEmptyAtAllLevels(taskConfig.SOURCE_DIR, sDeepDirName)

        assertTargetDirChildCount(1)
        assertDeepDirIsEmptyAtAllLevels(taskConfig.TARGET_DIR, sDeepDirName)

    }
}