package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.dirs.flat_dirs.re_creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import org.junit.Assert

abstract class ReCreatingDirsBase : SyncTestBase() {

    /**
     * Пересоздание каталогов после удаления после синхронизации.
     * Эти предшествующие события не должны влиять на воссозданные каталоги.
     *
     * Пересоздание единственного каталога в источнике сразу после удаления [re_creating_singleton_dir_in_source_before_sync]
     * Пересоздание единственного каталога в источнике после удаления с синхронизацией [re_creating_singleton_dir_in_source_after_sync]
     *
     * Пересоздание единственного каталога в приёмнике сразу после удаления [re_creating_singleton_dir_in_target_before_sync]
     * Пересоздание единственного каталога в приёмнике после удаления с синхронизацией [re_creating_singleton_dir_in_target_after_sync]
     *
     * Пересоздание второго каталога в источнике сразу после удаления [re_creating_second_dir_in_source_before_sync]
     * Пересоздание второго каталога в источнике после удаления с синхронизацией [re_creating_second_dir_in_target_after_sync]
     *
     * Пересоздание второго каталога в приёмнике сразу после удаления []
     * Пересоздание второго каталога в приёмнике после удаления с синхронизацией []
     */


    /**
     * [1] --- [1]
     *
     * x --- [1]
     *
     * [1] --- [1]
     *
     * sync
     *
     * [1] --- [1]
     *
     */
    open fun re_creating_singleton_dir_in_source_before_sync() {
        prepare()

        fileHelper.deleteDirFromSource(sDirName)
        fileHelper.createDirInSource(sDirName)

        doSync()
    }


    /**
     * [1] --- [1]
     *
     * x --- [1]
     *
     * sync
     *
     * x --- x
     *
     * [1] --- x
     *
     * sync
     *
     * [1] --- [1]
     */
    open fun re_creating_singleton_dir_in_source_after_sync() {
        prepare()

        fileHelper.deleteDirFromSource(sDirName)
        doSync()

        fileHelper.createDirInSource(sDirName)
        doSync()
    }


    /**
     * [1] --- [1]
     *
     * [1] --- x
     *
     * [1] --- [1]
     *
     * sync
     *
     * [1] --- [1]
     */
    open fun re_creating_singleton_dir_in_target_before_sync() {
        prepare()

        fileHelper.deleteDirFromTarget(sDirName)
        fileHelper.createDirInTarget(sDirName)

        doSync()
    }


    /**
     * Не имеет смысла, так как совпадает со случаем
     * удаления в приёмнике с последующей синхронизацией.
     */
    open fun re_creating_singleton_dir_in_target_after_sync() {

    }


    /**
     * [1] --- [1]
     *
     * [1][2] --- [1]
     *
     * [1]x --- [1]
     *
     * [1][2] --- [1]
     *
     * sync
     *
     * [1][2] --- [1][2]
     */
    open fun re_creating_second_dir_in_source_before_sync() {
        prepare()

        fileHelper.createDirInSource(commonDirName)
        fileHelper.deleteFileFromSource(commonDirName)
        fileHelper.createDirInSource(commonDirName)

        doSync()
    }


    /**
     * [1] --- x
     *
     * sync
     *
     * [1] --- [1]
     */
    private fun prepare() {
        fileHelper.createDirInSource(sDirName)
        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sDir)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(sDirInTarget)
    }
}