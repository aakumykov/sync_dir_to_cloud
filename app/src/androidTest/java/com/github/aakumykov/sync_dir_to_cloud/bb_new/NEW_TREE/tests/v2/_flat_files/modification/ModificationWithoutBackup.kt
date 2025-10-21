package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.byte_array_joined_string.joinedString
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File

class ModificationWithoutBackup : SyncTestBase() {

    /**
     * Файл в источнике изменился перед синхронизацией [source_file_was_changed_before_sync]
     * Файл в источнике изменился после синхронизации [source_file_was_changed_after_sync]
     */

    override val taskConfig: TaskConfig get() = localToLocalNoBackupTaskConfig

    @Test
    fun source_file_was_changed_before_sync() {
        repeat(1000) {
            fileHelper.createFileInSource(sFileName, sFileData)
            assertExistsAndContains(sFile, sFileData)

            val newData = randomBytes

            fileHelper.createFileInSource(sFileName, newData)
            assertExistsAndContains(sFile, newData)
        }
    }


    @Test
    fun source_file_was_changed_after_sync() {
        repeat(2) {
            fileHelper.createFileInSource(sFileName, sFileData)
            assertSourceDirChildCount(1)
            assertTargetDirChildCount(0)
            assertExistsAndContains(sFile, sFileData)

            doSync()

            assertTargetDirChildCount(1)
            assertExistsAndContains(sFileInTarget, sFileData)

            val newData = randomBytes

            fileHelper.createFileInSource(sFileName, newData)
            assertSourceDirChildCount(1)
            assertExistsAndContains(sFile, newData)
//            doSync()
//            Assert.assertTrue(sFileInTarget.exists())
//            assertExistsAndContains(sFileInTarget, newData)
        }
    }


    @Test
    fun simple_copy_with_manual_created_files_in_cache_dir() {

        val cacheDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir

        val sFile = File(cacheDir, randomName)
        val tFile = File(cacheDir, randomName)

        val data = randomBytes
        val newData = randomBytes

        sFile.createNewFile()
        Assert.assertTrue(sFile.exists())
        sFile.writeBytes(data)
        Assert.assertEquals(data.joinedString, sFile.readBytes().joinedString)

        sFile.copyTo(tFile).also {
            Assert.assertEquals(data.joinedString, it.readBytes().joinedString)
        }
    }


    @Test
    fun manual_file_create_copy_update_in_source_and_target_dir() {

        val data = randomBytes
        val newData = randomBytes
        Assert.assertFalse(data.isEmpty())
        Assert.assertFalse(newData.isEmpty())

        val sName = randomName
        val tName = randomName

        val sFile = File(taskConfig.SOURCE_DIR, sName)
        val tFile = File(taskConfig.TARGET_DIR, tName)

        // == Исходный файл ==
        // Создаю
        sFile.createNewFile().also { Assert.assertTrue(it) }
        Assert.assertTrue(sFile.exists())
        Assert.assertTrue(sFile.readBytes().isEmpty())

        sFile.writeBytes(data)
        Assert.assertFalse(sFile.readBytes().isEmpty())
        Assert.assertEquals(data.joinedString, sFile.readBytes().joinedString)

        // == Целевой файл ==
        // Копирую исходный в целевой
        Assert.assertFalse(tFile.exists())
        sFile.copyTo(tFile).also { Assert.assertTrue(tFile.exists()) }
        Assert.assertTrue(tFile.exists())
        Assert.assertEquals(data.joinedString, tFile.readBytes().joinedString)
        Assert.assertEquals(sFile.readBytes().joinedString, tFile.readBytes().joinedString)

        val sList = taskConfig.SOURCE_DIR.listFiles()?.map { it.name } ?: emptyList()
        val tList = taskConfig.TARGET_DIR.listFiles()?.map { it.name } ?: emptyList()
        println(sList)

        // Изменяю исходный
        sFile.writeBytes(newData)
        Assert.assertEquals(newData.joinedString, sFile.readBytes().joinedString)

        // Изменяю целевой
        sFile.copyTo(tFile, overwrite = true)
        Assert.assertEquals(newData.joinedString, tFile.readBytes().joinedString)
        Assert.assertEquals(sFile.readBytes().joinedString, tFile.readBytes().joinedString)
    }
}