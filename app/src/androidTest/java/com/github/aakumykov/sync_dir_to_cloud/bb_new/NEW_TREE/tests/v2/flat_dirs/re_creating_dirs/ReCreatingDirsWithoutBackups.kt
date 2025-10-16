package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.re_creating_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test

open class ReCreatingDirsWithoutBackups : ReCreatingDirsBase() {

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()



    // ==== sync ===>
    @Test
    override fun re_creating_singleton_dir_in_source_before_sync() {
        super.re_creating_singleton_dir_in_source_before_sync()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun re_creating_singleton_dir_in_source_after_sync() {
        super.re_creating_singleton_dir_in_source_after_sync()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun re_creating_singleton_dir_in_target_before_sync() {
        super.re_creating_singleton_dir_in_target_before_sync()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun re_creating_singleton_dir_in_target_after_sync() {
        super.re_creating_singleton_dir_in_target_after_sync()
        // Не требуется
    }


    // ==== sync ===>
    @Test
    override fun re_creating_second_dir_in_source_before_sync() {
        super.re_creating_second_dir_in_source_before_sync()

        Assert.assertEquals(2, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)
        assertExistsAndEmpty(commonDirInSource)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
        assertExistsAndEmpty(commonDirInTarget)
    }
}