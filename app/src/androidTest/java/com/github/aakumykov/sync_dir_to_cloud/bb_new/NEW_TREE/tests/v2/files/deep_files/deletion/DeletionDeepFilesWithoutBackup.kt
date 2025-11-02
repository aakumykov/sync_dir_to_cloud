package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_is_empty_as_all_levels.assertDeepDirIsEmptyAtAllLevels
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import org.junit.Assert
import org.junit.Test

class DeletionDeepFilesWithoutBackup : DeletionDeepFilesBase() {

    override val taskConfig: TaskConfig
        get() = localToLocalNoBackupTaskConfig


    @Test
    override fun deep_file_deletion_in_source() {
        super.deep_file_deletion_in_source()

        assertDeepDirIsEmptyAtAllLevels(taskConfig.SOURCE_DIR, sDeepDirName)

        assertSourceDirChildCount(1)
        Assert.assertFalse(sDeepFile.exists())

        assertTargetDirChildCount(1)
        Assert.assertFalse(sDeepFileInTarget.exists())
    }


    @Test
    override fun deep_file_deletion_in_target() {
        super.deep_file_deletion_in_target()

        assertSourceDirChildCount(1)
        Assert.assertTrue(sDeepFile.exists())

        assertTargetDirChildCount(1)
        Assert.assertTrue(sDeepFileInTarget.exists())
    }


    @Test
    override fun deep_files_deletion_in_source_and_target() {
        super.deep_files_deletion_in_source_and_target()

        assertSourceDirChildCount(1)
        Assert.assertFalse(sDeepFile.exists())

        assertTargetDirChildCount(1)
        Assert.assertFalse(sDeepFileInTarget.exists())
    }
}