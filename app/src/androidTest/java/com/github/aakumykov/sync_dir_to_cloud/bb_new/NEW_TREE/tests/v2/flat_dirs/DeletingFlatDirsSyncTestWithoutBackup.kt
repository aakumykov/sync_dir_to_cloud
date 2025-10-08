package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test

class DeletingFlatDirsSyncTestWithoutBackup : SyncTestBase()  {

    // TODO: доделать

    /**
     * Исходное состояние 1: Одноимённые пустые каталоги.
     * - удаление каталога в источнике [same_dirs_deleting_dir_from_source]
     * - удаление каталога в приёмнике [same_dirs_deleting_dir_from_target]
     * - удаление в обоих местах [same_dirs_deleting_dir_from_source_and_target]
     *
     * Исходное состояние 2: Разноимённые пустые каталоги.
     * - удаление одного в источнике [diff_dirs_deleting_dir_from_source]
     * - удаление обоих в источнике [diff_dirs_deleting_dir_from_target]

     * - удаление одного в приёмнике []
     * - удаление обоих в приёмнике []
     *
     * - удаление одноимённых там и там []
     * - удаление разноимённых там и там []
     *
     * (- удаление всех, кроме одного в источнике
     * - удаление всех, кроме одного в приёмнике)
     */

    companion object {
        private val COMMON_DIR_NAME = randomName
        private val S_DIR_NAME = randomName
        private val T_DIR_NAME = randomName
    }

    private fun create_same_name_dirs_on_both_sides() {
        fileHelper.createDirInSource(COMMON_DIR_NAME).also { Assert.assertTrue(it.exists()) }
        fileHelper.createDirInTarget(COMMON_DIR_NAME).also { Assert.assertTrue(it.exists()) }
        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }

    private fun create_diff_name_dirs_on_both_sides() {
        fileHelper.createDirInSource(S_DIR_NAME).also { Assert.assertTrue(it.exists()) }
        fileHelper.createDirInTarget(T_DIR_NAME).also { Assert.assertTrue(it.exists()) }
        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }

    private val commonDirInSource = fileHelper.dirInSource(COMMON_DIR_NAME)
    private val commonDirInTarget = fileHelper.dirInTarget(COMMON_DIR_NAME)

    private val sDir= fileHelper.createDirInSource(S_DIR_NAME)
    private val tDir= fileHelper.createDirInTarget(T_DIR_NAME)

    private val sDirInTarget= fileHelper.dirInTarget(S_DIR_NAME)
    private val tDirInSource= fileHelper.dirInSource(T_DIR_NAME)


    // ===== SYNC =====>
    @Test
    fun same_dirs_deleting_dir_from_source() {
        create_same_name_dirs_on_both_sides()
        doSync()

        fileHelper.deleteDirFromSource(COMMON_DIR_NAME)
        doSync()

        Assert.assertFalse(commonDirInSource.exists())
        Assert.assertFalse(commonDirInTarget.exists())

        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertTrue(taskConfig.TARGET_DIR.isEmpty)
    }


    // ===== SYNC =====>
    @Test
    fun same_dirs_deleting_dir_from_target() {
        create_same_name_dirs_on_both_sides()
        doSync()

        fileHelper.deleteDirFromTarget(COMMON_DIR_NAME)
        doSync()

        Assert.assertTrue(commonDirInSource.exists())
        Assert.assertTrue(commonDirInTarget.exists())

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }


    // ===== SYNC =====>
    @Test
    fun same_dirs_deleting_dir_from_source_and_target() {
        create_same_name_dirs_on_both_sides()
        doSync()

        fileHelper.deleteDirFromSource(COMMON_DIR_NAME)
        fileHelper.deleteDirFromTarget(COMMON_DIR_NAME)
        doSync()

        Assert.assertFalse(commonDirInSource.exists())
        Assert.assertFalse(commonDirInTarget.exists())

        Assert.assertTrue(taskConfig.SOURCE_DIR.isEmpty)
        Assert.assertTrue(taskConfig.TARGET_DIR.isEmpty)
    }


    // ===== SYNC =====>
    // [1] --- [2]
    // sync
    //  x --- [2]
    // sync
    //  результат: x --- [2]
    @Test
    fun diff_dirs_deleting_dir_from_source() {
        create_diff_name_dirs_on_both_sides()
        doSync()

        fileHelper.deleteDirFromSource(S_DIR_NAME)
        doSync()

        // Каталог в источнике не появился
        Assert.assertFalse(sDir.exists())
        // Каталог из приёмника удалился
        Assert.assertFalse(sDirInTarget.exists())

        // Каталог приёмника в нём сохранился.
        Assert.assertTrue(tDir.exists())
        // И в источнике не появился.
        Assert.assertFalse(tDirInSource.exists())

        // В источнике стало 0 каталогов.
        Assert.assertEquals(0, fileHelper.listSourceDir().size)
        // В приёмнике стал 1 каталог.
        Assert.assertEquals(1, fileHelper.listTargetDir().size)

        // Каталог приёмника остался пустым.
        Assert.assertTrue(tDir.isEmpty)
    }


    // ===== SYNC =====>
    // [1] --- [2]
    // sync
    //  [1] --- x
    // sync
    //  результат: [1] --- [1]
    @Test
    fun diff_dirs_deleting_dir_from_target() {
        create_diff_name_dirs_on_both_sides()
        doSync()

        fileHelper.deleteDirFromTarget(T_DIR_NAME)
        doSync()

        Assert.assertTrue(sDir.exists())
        Assert.assertFalse(tDir.exists())

        Assert.assertTrue(sDirInTarget.exists())
        Assert.assertFalse(tDirInSource.exists())

        Assert.assertEquals(1, taskConfig.SOURCE_DIR.list()!!.size)
        Assert.assertEquals(1, taskConfig.TARGET_DIR.list()!!.size)

        Assert.assertTrue(sDir.isEmpty)
        Assert.assertTrue(sDirInTarget.isEmpty)
    }
}