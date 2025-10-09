package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test

class CreatingFlatDirsWithoutBackup : SyncTestBase() {

    /**
     * Создание дополнительного каталога в источнике [create_additional_dir_in_source]
     * Создание дополнительного каталога в приёмнике [create_additional_dir_in_target]
     * Создание дополнительного каталога в обоих местах [create_additional_dir_in_source_and_target]
     */

    private val sDirName = randomName
    private val tDirName = randomName

    private val dirInSource get() = fileHelper.dirInSource(sDirName)
    private val dirInTarget get() = fileHelper.dirInTarget(tDirName)

    // ===== sync =====>
    // [1] --- x
    // sync
    // [1] --- [1]
    // [1][2] --- [1]
    // sync
    // [1][2] --- [1][2]
    @Test
    fun create_additional_dir_in_source() {
        create_dir_in_source_and_sync_with_target()

        val newDirName = randomName
        val newDirInSource = fileHelper.createDirInSource(newDirName)
        val newDirInTarget = fileHelper.dirInTarget(newDirName)

        doSync()

        Assert.assertTrue(newDirInSource.exists())
        Assert.assertTrue(newDirInTarget.exists())

        Assert.assertTrue(newDirInSource.isEmpty)
        Assert.assertTrue(newDirInTarget.isEmpty)

        Assert.assertEquals(2, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)
    }

    // ===== sync =====>
    // [1] --- x
    // sync
    // [1] --- [1]
    // [1] --- [1][2]
    // sync
    // [1] -- [1][2]
    @Test
    fun create_additional_dir_in_target() {
        create_dir_in_source_and_sync_with_target()

        val newDirName = randomName
        val newDirInTarget = fileHelper.createDirInTarget(newDirName)
        val newDirInSource = fileHelper.dirInSource(newDirName)

        doSync()

        // "Старые" каталоги
        Assert.assertTrue(dirInSource.exists())
        Assert.assertTrue(dirInTarget.exists())

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)

        Assert.assertTrue(dirInSource.isEmpty)
        Assert.assertTrue(dirInTarget.isEmpty)

        // Новый каталог
        Assert.assertFalse()
    }



    private fun create_dir_in_source_and_sync_with_target() {
        fileHelper.createDirInSource(sDirName)
        doSync()

        Assert.assertTrue(dirInSource.exists())
        Assert.assertTrue(dirInTarget.exists())

        Assert.assertTrue(dirInSource.isEmpty)
        Assert.assertTrue(dirInTarget.isEmpty)

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }
}