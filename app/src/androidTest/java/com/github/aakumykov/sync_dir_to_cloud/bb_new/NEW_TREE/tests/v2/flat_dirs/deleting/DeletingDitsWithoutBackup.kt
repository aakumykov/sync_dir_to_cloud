package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.deleting

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import org.junit.Assert
import org.junit.Test

class DeletingDitsWithoutBackup : DeletingDirsBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    // [1] --- [1]
    // x --- [1]
    // sync
    // x --- x
    @Test
    override fun same_dirs_deleting_dir_from_source() {
        super.same_dirs_deleting_dir_from_source()
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertTrue(taskConfig.TARGET_DIR.isEmpty)
    }


    // [1] --- [1]
    // [1] --- x
    // sync
    // [1] --- [1]
    @Test
    override fun same_dirs_deleting_dir_from_target() {
        super.same_dirs_deleting_dir_from_target()
        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(commonDirInSource)
        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(commonDirInTarget)
    }


    // [1] --- [1]
    // x --- x
    // sync
    // x --- x
    @Test
    override fun same_dirs_deleting_dir_from_source_and_target() {
        super.same_dirs_deleting_dir_from_source_and_target()
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertTrue(taskConfig.TARGET_DIR.isEmpty)
    }



    // [1] --- [1][2]
    // x --- [1][2]
    // sync
    // x --- [2]
    @Test
    override fun diff_dirs_deleting_dir_from_source() {
        super.diff_dirs_deleting_dir_from_source()
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
    }


    // [1] --- [1][2]
    // [1] --- [1]x
    // sync
    // [1] --- [1]
    @Test
    override fun diff_dirs_deleting_dir_from_target() {
        super.diff_dirs_deleting_dir_from_target()
        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)
        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(sDirInTarget)
    }


    // [1] --- [1][2]
    // x --- [1][2]
    // sync
    // x --- [2]
    @Test
    override fun diff_dirs_deleting_all_in_source() {
        super.diff_dirs_deleting_all_in_source()
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
    }


    // [1] --- [1][2]
    // [1] --- x
    // sync
    // [1] --- [1]
    @Test
    override fun diff_dirs_deleting_all_in_target() {
        super.diff_dirs_deleting_all_in_target()
        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)
        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(sDirInTarget)
    }


    // [1] --- [1][2]
    // x --- x[2]
    // sync
    // x --- [2]
    @Test
    override fun diff_dirs_deleting_same_dirs_in_both_places() {
        super.diff_dirs_deleting_same_dirs_in_both_places()
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
    }


    // [1] --- [1][2]
    // x --- [1]x
    // sync
    // x --- x
    @Test
    override fun diff_dirs_deleting_diff_dirs_in_both_places() {
        super.diff_dirs_deleting_diff_dirs_in_both_places()
        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertTrue(taskConfig.TARGET_DIR.isEmpty)
    }
}