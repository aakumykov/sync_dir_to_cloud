package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test

class ReCreatingFlatDirsWithoutBackup : CreateAndSyncTestBase()  {

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


    // [1] --- [1]
    // x --- [1]
    // [1] --- [1]
    // sync
    // [1] --- [1]
    @Test
    fun re_creating_singleton_dir_in_source_before_sync() {
        prepare()

        fileHelper.deleteDirFromSource(dirInSourceName)
        fileHelper.createDirInSource(dirInSourceName)

        doSync()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(dirInSource)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }


    // [1] --- [1]
    // x --- [1]
    // sync
    // x --- x
    // [1] --- x
    // sync
    // [1] --- [1]
    @Test
    fun re_creating_singleton_dir_in_source_after_sync() {
        prepare()

        fileHelper.deleteDirFromSource(dirInSourceName)
        doSync()

        fileHelper.createDirInSource(dirInSourceName)
        doSync()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(dirInSource)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }


    // [1] --- [1]
    // [1] --- x
    // [1] --- [1]
    // sync
    // [1] --- [1]
    @Test
    fun re_creating_singleton_dir_in_target_before_sync() {
        prepare()

        fileHelper.deleteDirFromTarget(dirInSourceName)
        fileHelper.createDirInTarget(dirInSourceName)

        doSync()

        Assert.assertEquals(1, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(dirInSource)

        Assert.assertEquals(1, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
    }



    @Test
    fun re_creating_singleton_dir_in_target_after_sync() {
        // Не имеет смысла, так как совпадает со случаем
        // удаления в приёмнике с последующей синхронизацией.
    }


    // [1] --- [1]
    // [1][2] --- [1]
    // [1]x --- [1]
    // [1][2] --- [1]
    // sync
    // [1][2] --- [1][2]
    @Test
    fun re_creating_second_dir_in_source_before_sync() {
        prepare()

        val newName = randomName
        val newDirInSource = fileHelper.dirInSource(newName)
        val newDirInTarget = fileHelper.dirInTarget(newName)

        fileHelper.createDirInSource(newName)
        fileHelper.deleteFileFromSource(newName)
        fileHelper.createDirInSource(newName)

        doSync()

        Assert.assertEquals(2, fileHelper.countSourceDirItems())
        assertExistsAndEmpty(dirInSource)
        assertExistsAndEmpty(newDirInSource)

        Assert.assertEquals(2, fileHelper.countTargetDirItems())
        assertExistsAndEmpty(sDirInTarget)
        assertExistsAndEmpty(newDirInTarget)
    }



}