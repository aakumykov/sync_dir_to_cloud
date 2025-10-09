package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.deep_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import org.junit.Assert
import org.junit.Test

class StaticDeepDirsWithoutBackup : SyncTestBase() {

    /**
     * Просто "существование" каталогов.
     *
     * Глубокие пустые каталоги - пусто [deep_empty_dirs_in_source]
     * Пусто - глубокие пустые каталоги [deep_empty_dirs_in_target]
     *
     * Одноимённыя глубокия каталоги в источнике и приёмнике [same_name_deep_empty_dirs_in_source_and_target]
     * Разноимённыя глубокия каталоги в источнике и приёмнике [diff_name_deep_dirs_in_source_and_target]
     */

    // ====== sync =====>
    @Test
    fun deep_empty_dirs_in_source() {
        val deepDirName = randomDeepDirName

        val sDeepDir = fileHelper.createDirInSource(deepDirName)
        val tDeepDir = fileHelper.dirInTarget(deepDirName)

        doSync()

        Assert.assertTrue(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }


    // ====== sync =====>
    @Test
    fun deep_empty_dirs_in_target() {
        val deepDirName = randomDeepDirName

        val sDeepDir = fileHelper.dirInSource(deepDirName)
        val tDeepDir = fileHelper.createDirInTarget(deepDirName)

        doSync()

        Assert.assertFalse(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertEquals(0, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }



    // ====== sync =====>
    @Test
    fun same_name_deep_empty_dirs_in_source_and_target() {
        val deepDirName = randomDeepDirName

        val sDeepDir = fileHelper.createDirInSource(deepDirName)
        val tDeepDir = fileHelper.createDirInTarget(deepDirName)

        doSync()

        Assert.assertTrue(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertTrue(sDeepDir.isEmpty)
        Assert.assertTrue(tDeepDir.isEmpty)

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }


    // ====== sync =====>
    @Test
    fun diff_name_deep_dirs_in_source_and_target() {
        val sDeepDirName = randomDeepDirName
        val tDeepDirName = randomDeepDirName

        val sDeepDir = fileHelper.createDirInSource(sDeepDirName)
        val tDeepDir = fileHelper.createDirInTarget(tDeepDirName)

        val sDeepDirInTarget = fileHelper.dirInTarget(sDeepDirName)
        val tDeepDirInSource = fileHelper.dirInSource(tDeepDirName)

        doSync()

        Assert.assertTrue(sDeepDir.exists())
        Assert.assertTrue(tDeepDir.exists())

        Assert.assertTrue(sDeepDirInTarget.exists())
        Assert.assertFalse(tDeepDirInSource.exists())

        // Теоретически, часть пути может совпасть, и каталог окажется непустым,
        // но с UUID-именами эта вероятность очень низкая.
        Assert.assertTrue(sDeepDir.isEmpty)
        Assert.assertTrue(tDeepDir.isEmpty)

        Assert.assertTrue(sDeepDirInTarget.isEmpty)

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(2, fileHelper.listTargetDir().size)
    }
}