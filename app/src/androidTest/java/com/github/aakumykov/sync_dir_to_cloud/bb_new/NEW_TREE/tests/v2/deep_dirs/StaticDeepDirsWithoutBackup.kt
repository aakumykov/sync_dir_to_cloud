package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.deep_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import org.junit.Assert
import org.junit.Test

open class StaticDeepDirsWithoutBackup : StaticDeepDirsBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    @Test
    override fun deep_empty_dir_in_source() {
        super.deep_empty_dir_in_source()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(commonDeepDirInSource)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(commonDeepDirInTarget)
    }


    @Test
    override fun deep_empty_dir_in_target() {
        super.deep_empty_dir_in_target()

        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(commonDeepDirInTarget)
    }


    @Test
    override fun same_name_deep_empty_dirs_in_source_and_target() {
        super.same_name_deep_empty_dirs_in_source_and_target()

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        assertExistsAndEmpty(commonDeepDirInSource)

        Assert.assertEquals(1, fileHelper.listTargetDir().size)
        assertExistsAndEmpty(commonDeepDirInTarget)
    }


    @Test
    override fun diff_name_deep_dirs_in_source_and_target() {
        super.diff_name_deep_dirs_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDeepDir)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(tDeepDir)
        assertExistsAndEmpty(sDeepDirInTarget)
    }
}