package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Assert
import org.junit.Test

class CreatingFlatDirs : FlatDirsBase() {

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
    fun only_in_source_dir() {
        fileHelper.createDirInSource(sDirName)
        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(sDirInTarget)
    }


    // ==== sync ===>
    @Test
    fun only_in_target_dir() {
        fileHelper.createDirInTarget(tDirName)
        doSync()

        Assert.assertEquals(0, fileHelper.countFilesInSource())

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
    }


    // ==== sync ===>
    @Test
    fun same_name_dirs_in_source_and_target() {
        fileHelper.createDirInSource(commonDirName)
        fileHelper.createDirInTarget(commonDirName)
        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
    }


    // ==== sync ===>
    @Test
    fun diff_name_dirs_in_source_and_target() {
        var sourceList = taskConfig.SOURCE_DIR.list()
        var targetList = taskConfig.TARGET_DIR.list()

        fileHelper.createDirInSource(sDirName)
        fileHelper.createDirInTarget(tDirName)
         sourceList = taskConfig.SOURCE_DIR.list()
         targetList = taskConfig.TARGET_DIR.list()

        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)
        sourceList = taskConfig.SOURCE_DIR.list()

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
        targetList = taskConfig.TARGET_DIR.list()
        assertExistsAndEmpty(sFileInTarget)
    }
}