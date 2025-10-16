package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.creating_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test

open class CreatingFlatDirsWithoutBackup : CreatingDirsBase() {

    /**
     * Каталога только в источнике [only_in_source_dir]
     * Каталог только в приёмнике [only_in_target_dir]
     *
     * Одноимённые каталоги в источнике и приёмнике [same_name_dirs_in_source_and_target]
     * Разноимённые каталоги в источнике и приёмнике [diff_name_dirs_in_source_and_target]
     */

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithoutBackupTaskConfig()


    // ==== sync ===>
    @Test
    override fun only_in_source_dir() {
        super.only_in_source_dir()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun only_in_target_dir() {
        super.only_in_target_dir()

        Assert.assertEquals(0, fileHelper.countSourceDirItems())

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(tDir)
    }


    // ==== sync ===>
    @Test
    override fun same_name_dirs_in_source_and_target() {
        super.same_name_dirs_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(commonDirInSource)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(commonDirInTarget)
    }


    // ==== sync ===>
    @Test
    override fun diff_name_dirs_in_source_and_target() {
        super.diff_name_dirs_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(tDir)
        assertExistsAndEmpty(sDirInTarget)
    }

}