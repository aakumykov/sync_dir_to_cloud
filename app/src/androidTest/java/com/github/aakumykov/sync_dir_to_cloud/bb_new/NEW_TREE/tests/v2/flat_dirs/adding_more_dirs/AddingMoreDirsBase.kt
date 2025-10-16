package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs.adding_more_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert

abstract class AddingMoreDirsBase : SyncTestBase() {

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

    protected val newSDirName = randomName
    protected val newTDirName = randomName

    protected val commonNewDirName = randomName

    protected val newSDir = fileHelper.dirInSource(newSDirName)
    protected val newTDir = fileHelper.dirInTarget(newTDirName)

    protected val newSDirInTarget = fileHelper.dirInTarget(newSDirName)
    protected val newTDirInSource = fileHelper.dirInSource(newTDirName)

    protected val newCommonSDir = fileHelper.dirInSource(commonNewDirName)
    protected val newCommonTDir = fileHelper.dirInTarget(commonNewDirName)


    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1][2] --- [1]
    // ===== sync =====>
    // [1][2] --- [1][2]
    open fun create_additional_dir_in_source() {
        prepare()
        fileHelper.createDirInSource(newSDirName)
        doSync()
    }

    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1] --- [1][2]
    // ===== sync =====>
    // [1] -- [1][2]
    open fun create_additional_dir_in_target() {
        prepare()
        fileHelper.createDirInTarget(newTDirName)
        doSync()
    }

    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1][2] --- [1][3]
    // ===== sync =====>
    // [1][2] --- [1][2][3]
    open fun create_diff_names_additional_dirs_in_source_and_target() {
        prepare()
        fileHelper.createDirInSource(newSDirName)
        fileHelper.createDirInTarget(newTDirName)
        doSync()
    }


    // [1] --- x
    // ===== sync =====>
    // [1] --- [1]
    // [1][2] --- [1][2]
    // sync
    // [1][2] --- [1][2]
    open fun create_same_name_additional_dirs_in_source_and_target() {
        prepare()
        fileHelper.createDirInSource(commonNewDirName)
        fileHelper.createDirInTarget(commonNewDirName)
        doSync()
    }


    open fun preparation_method_test() {
        prepare()
    }
    
    // [1] --- x
    // sync
    // [1] --- [1]
    private fun prepare() {
        fileHelper.createDirInSource(sDirName)
        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(sDirInTarget)
    }
}