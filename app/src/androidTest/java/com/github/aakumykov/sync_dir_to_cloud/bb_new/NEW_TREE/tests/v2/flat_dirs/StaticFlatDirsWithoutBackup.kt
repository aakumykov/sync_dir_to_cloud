package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test

class StaticFlatDirsWithoutBackup : SyncTestBase() {

    /**
     * Нет ничего - нет ничего [empty_source_and_target_sync]
     *
     * Пустой каталог - нет ничего [one_empty_dir_in_source]
     * Нет ничего - пустой каталог [one_empty_dir_in_target]
     *
     * Пустой каталог - одноимённый пустой каталог [same_name_empty_dirs_in_source_and_target]
     * Пустой каталог - разноимённый пустой каталог [diff_name_empty_dirs_in_source_and_target]
     */


    @Test
    fun empty_source_and_target_sync() {
        doSync()
        Assert.assertEquals(0, fileHelper.listSourceDir().size)
        Assert.assertEquals(0, fileHelper.listTargetDir().size)
    }


    @Test
    fun one_empty_dir_in_source() {
        val dirName = randomName
        fileHelper.createDirInSource(dirName)
        doSync()
        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        Assert.assertTrue(fileHelper.dirInSource(dirName).exists())
        Assert.assertTrue(fileHelper.dirInTarget(dirName).exists())

        Assert.assertEquals(0, fileHelper.listDirInSource(dirName).size)
        Assert.assertEquals(0, fileHelper.listDirInTarget(dirName).size)
    }

    @Test
    fun one_empty_dir_in_target() {
        val dirName = randomName

        val sDir = fileHelper.dirInSource(dirName)
        val tDir = fileHelper.createDirInTarget(dirName)

        doSync()

        Assert.assertEquals(0, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        Assert.assertFalse(sDir.exists())
        Assert.assertTrue(tDir.exists())

        Assert.assertTrue(tDir.isEmpty)
    }


    @Test
    fun same_name_empty_dirs_in_source_and_target() {
        val dirName = randomName
        val sDir = fileHelper.createDirInSource(dirName)
        val tDir = fileHelper.createDirInTarget(dirName)
        doSync()

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        Assert.assertTrue(fileHelper.dirInSource(dirName).exists())
        Assert.assertTrue(fileHelper.dirInTarget(dirName).exists())

        Assert.assertTrue(sDir.isEmpty)
        Assert.assertTrue(tDir.isEmpty)
    }

    @Test
    fun diff_name_empty_dirs_in_source_and_target() {
        val dirNameS = randomName
        val dirNameT = randomName

        fileHelper.createDirInSource(dirNameS)
        fileHelper.createDirInTarget(dirNameT)

        doSync()

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)

        // Исходные каталоги
        fileHelper.dirInSource(dirNameS).also {
            Assert.assertTrue(it.exists())
            Assert.assertTrue(it.isEmpty)
        }
        fileHelper.dirInTarget(dirNameT).also {
            Assert.assertTrue(it.exists())
            Assert.assertTrue(it.isEmpty)
        }

        // Созданный синхронизацией каталога
        fileHelper.dirInTarget(dirNameS).also {
            Assert.assertTrue(it.exists())
            Assert.assertTrue(it.isEmpty)
        }
    }
}