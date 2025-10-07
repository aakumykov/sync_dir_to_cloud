package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import org.junit.Assert
import org.junit.Test

class DeepSyncTestWithoutBackup : SyncTestBase() {

    /**
     * Глубокие пустые каталоги - пусто [deep_empty_dirs_in_source]
     * Пусто - глубокие пустые каталоги [deep_empty_dirs_in_target]
     */

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
}