package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.flat_dirs.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import org.junit.Assert

abstract class DeletingDirsBase : SyncTestBase()  {

    /**
     * Исходное состояние 1: Одноимённые пустые каталоги.
     *
     * - удаление каталога в источнике [same_dirs_deleting_dir_from_source]
     * - удаление каталога в приёмнике [same_dirs_deleting_dir_from_target]
     * - удаление в обоих местах [same_dirs_deleting_dir_from_source_and_target]
     *
     * Исходное состояние 2: Разноимённые пустые каталоги.
     *
     * - удаление одного в источнике [diff_dirs_deleting_dir_from_source]
     * - удаление одного в приёмнике [diff_dirs_deleting_dir_from_target]

     * - удаление всех в источнике [diff_dirs_deleting_all_in_source]
     * - удаление всех в приёмнике [diff_dirs_deleting_all_in_target]
     *
     * - удаление одноимённых там и там [diff_dirs_deleting_same_dirs_in_both_places]
     * - удаление разноимённых там и там [diff_dirs_deleting_diff_dirs_in_both_places]
     *
     * (- удаление всех, кроме одного в источнике
     * - удаление всех, кроме одного в приёмнике)
     */

    open fun same_dirs_deleting_dir_from_source() {
        prepare_same_name_dirs()
        fileHelper.deleteDirFromSource(commonDirName)
        doSync()
    }

    open fun same_dirs_deleting_dir_from_target() {
        prepare_same_name_dirs()
        fileHelper.deleteDirFromTarget(commonDirName)
        doSync()
    }

    open fun same_dirs_deleting_dir_from_source_and_target() {
        prepare_same_name_dirs()
        fileHelper.deleteDirFromSource(commonDirName)
        fileHelper.deleteDirFromTarget(commonDirName)
        doSync()
    }


    open fun diff_dirs_deleting_dir_from_source() {
        prepare_diff_name_dirs()
        fileHelper.deleteDirFromSource(sDirName)
        doSync()
    }

    open fun diff_dirs_deleting_dir_from_target() {
        prepare_diff_name_dirs()
        fileHelper.deleteDirFromTarget(tDirName)
        doSync()
    }

    open fun diff_dirs_deleting_all_in_source() {
        prepare_diff_name_dirs()
        fileHelper.deleteAllFilesInDir(taskConfig.SOURCE_DIR)
        doSync()
    }

    open fun diff_dirs_deleting_all_in_target() {
        prepare_diff_name_dirs()
        fileHelper.deleteAllFilesInDir(taskConfig.TARGET_DIR)
        doSync()
    }

    open fun diff_dirs_deleting_same_dirs_in_both_places() {
        prepare_diff_name_dirs()
        fileHelper.deleteDirFromSource(sDirName)
        fileHelper.deleteDirFromTarget(sDirName)
        doSync()
    }

    open fun diff_dirs_deleting_diff_dirs_in_both_places() {
        prepare_diff_name_dirs()
        fileHelper.deleteDirFromSource(sDirName)
        fileHelper.deleteDirFromTarget(tDirName)
        doSync()
    }



    // ==== sync ===>
    private fun prepare_same_name_dirs() {
        fileHelper.createDirInSource(commonDirName)
        fileHelper.createDirInTarget(commonDirName)

        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(commonDirInSource)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(commonDirInTarget)
    }

    // ==== sync ===>
    private fun prepare_diff_name_dirs() {
        fileHelper.createDirInSource(sDirName)
        fileHelper.createDirInTarget(tDirName)

        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
        assertExistsAndEmpty(sDirInTarget)
    }
}