package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.adding_new_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test

abstract class AddingNewDirsBase : SyncTestBase() {

    /**
     * Эти действия выполняются после первичной синхронизации (s) --> (t).
     *
     * TODO: по идее, первичная синхронизация должна быть ещё и в варианте (s) <-- (t)
     *  Но не слишком ли это? Если что не так, более сложные тесты это покажут...
     *
     * Создание дополнительного каталога в источнике [create_additional_dir_in_source]
     * Создание дополнительного каталога в приёмнике [create_additional_dir_in_target]
     *
     * Создание разных дополнительных каталогов в обоих местах [create_diff_names_additional_dirs_in_source_and_target]
     * Создание одинаковых дополнительных каталогов в обоих местах [create_same_name_additional_dirs_in_source_and_target]
     */

    protected val sNewDirName = randomName
    protected val tNewDirName = randomName

    protected val commonNewDirName = randomName

    protected val newSDir = fileHelper.dirInSource(sNewDirName)
    protected val newTDir = fileHelper.dirInTarget(tNewDirName)

    protected val newSDirInTarget = fileHelper.dirInTarget(sNewDirName)
    protected val newTDirInSource = fileHelper.dirInSource(tNewDirName)

    protected val sNewCommonDir = fileHelper.dirInSource(commonNewDirName)
    protected val tNewCommonDir = fileHelper.dirInTarget(commonNewDirName)


    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1][2] --- [1]
    // ===== sync =====>
    // [1][2] --- [1][2]
    @Test
    open fun create_additional_dir_in_source() {
        prepare()
        fileHelper.createDirInSource(sNewDirName)
        doSync()
    }

    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1] --- [1][2]
    // ===== sync =====>
    // [1] -- [1][2]
    @Test
    open fun create_additional_dir_in_target() {
        prepare()
        fileHelper.createDirInTarget(tNewDirName)
        doSync()
    }

    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1][2] --- [1][3]
    // ===== sync =====>
    // [1][2] --- [1][2][3]
    @Test
    open fun create_diff_names_additional_dirs_in_source_and_target() {
        prepare()
        fileHelper.createDirInSource(sNewDirName)
        fileHelper.createDirInTarget(tNewDirName)
        doSync()
    }


    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1][2] --- [1][2]
    // sync
    // [1][2] --- [1][2]
    @Test
    open fun create_same_name_additional_dirs_in_source_and_target() {
        prepare()
        fileHelper.createDirInSource(commonNewDirName)
        fileHelper.createDirInTarget(commonNewDirName)
        doSync()
    }



    private fun prepare() {
        fileHelper.createDirInSource(sDirName)
        fileHelper.createDirInTarget(tDirName)

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tDir)
    }
}