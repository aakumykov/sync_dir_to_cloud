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
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class ModificationWithoutBackup(val numberOfRun: Int) : SyncTestBase() {

    companion object {
        const val RUN_TEST_N_TIMES = 2

        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Int> = List(RUN_TEST_N_TIMES) { it }
    }

    override val taskConfig: TaskConfig get() = localToLocalNoBackupTaskConfig

    /**
     * Файл в источнике изменился перед синхронизацией [source_file_was_changed_before_sync]
     * Файл в источнике изменился после синхронизации [source_file_was_changed_after_sync]
     */

    @Test
    fun empty_test_() {
        Assert.assertTrue(true)
    }

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

        fileHelper.createFileInSource(sFileName, sFileData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(0)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, sFileData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)

        val newData = randomBytes

        fileHelper.createFileInSource(sFileName, newData)

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, sFileData)

        doSync()

        assertSourceDirChildCount(1)
        assertExistsAndContains(sFile, newData)
        assertTargetDirChildCount(1)
        assertExistsAndContains(sFileInTarget, newData)

//        assertExistsAndContains(sFileInTarget, sFileData)
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
    fun manual_operations_without_do_sync() {

        println("=== manual_operations_without_do_sync() ===")
        println("taskConfig.SOURCE_DIR (${taskConfig.SOURCE_DIR.exists()}): ${taskConfig.SOURCE_DIR.absolutePath}")
        println("taskConfig.TARGET_DIR (${taskConfig.TARGET_DIR.exists()}): ${taskConfig.TARGET_DIR.absolutePath}")

        val data = randomBytes
        val newData = randomBytes
        Assert.assertFalse(data.isEmpty())
        Assert.assertFalse(newData.isEmpty())

        val sourceFileName = randomName
        val targetFileName = randomName

        val sourceFile = File(taskConfig.SOURCE_DIR, sourceFileName)
        val targetFile = File(taskConfig.TARGET_DIR, targetFileName)

        // == Исходный файл ==
        // Создаю
        sourceFile.createNewFile().also { Assert.assertTrue(it) }
        Assert.assertTrue(sourceFile.exists())
        Assert.assertTrue(sourceFile.readBytes().isEmpty())

//        sFile.writeBytes(data)
        data.inputStream().use { inputStream ->
            sourceFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Assert.assertFalse(sourceFile.readBytes().isEmpty())
        Assert.assertEquals(data.joinedString, sourceFile.readBytes().joinedString)

        // == Целевой файл ==
        // Копирую исходный в целевой
        targetFile.createNewFile().also { Assert.assertTrue(it) }
        Assert.assertTrue(targetFile.exists())
//        sFile.copyTo(tFile).also { Assert.assertTrue(tFile.exists()) }
        sourceFile.inputStream().use { inputStream ->
            targetFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Assert.assertTrue(targetFile.exists())
        Assert.assertEquals(data.joinedString, targetFile.readBytes().joinedString)
        Assert.assertEquals(sourceFile.readBytes().joinedString, targetFile.readBytes().joinedString)

        val sList = taskConfig.SOURCE_DIR.listFiles()?.map { it.name } ?: emptyList()
        val tList = taskConfig.TARGET_DIR.listFiles()?.map { it.name } ?: emptyList()
        println(sList)

        // Изменяю исходный
//        sFile.writeBytes(newData)
        newData.inputStream().use { inputStream ->
            sourceFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Assert.assertEquals(newData.joinedString, sourceFile.readBytes().joinedString)

        // Копирую обновлённый исходный в целевой
//        sFile.copyTo(tFile, overwrite = true)
        sourceFile.inputStream().use { inputStream ->
            targetFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Assert.assertEquals(newData.joinedString, targetFile.readBytes().joinedString)
        Assert.assertEquals(sourceFile.readBytes().joinedString, targetFile.readBytes().joinedString)
    }


    @Test
    fun sync_of_manual_created_files() {

        val sFileName = "s_file.bin"

        val sFile = File(taskConfig.SOURCE_DIR, sFileName)
        val sFileInTarget = File(taskConfig.TARGET_DIR, sFileName)

        val initialData = randomBytes
        val updatedData = randomBytes

        Assert.assertEquals(0, taskConfig.SOURCE_DIR.listFiles()!!.size)
        Assert.assertEquals(0, taskConfig.TARGET_DIR.listFiles()!!.size)

        sFile.createNewFile()
        sFile.writeBytes(initialData)

        Assert.assertEquals(1, taskConfig.SOURCE_DIR.listFiles()!!.size)
        Assert.assertEquals(0, taskConfig.TARGET_DIR.listFiles()!!.size)

        Assert.assertTrue(sFile.exists())
        Assert.assertEquals(initialData.joinedString, sFile.readBytes().joinedString)

        doSync()

        Assert.assertEquals(1, taskConfig.SOURCE_DIR.listFiles()!!.size)
        Assert.assertEquals(1, taskConfig.TARGET_DIR.listFiles()!!.size)

        Assert.assertTrue(sFileInTarget.exists())
        Assert.assertEquals(initialData.joinedString, sFileInTarget.readBytes().joinedString)
        Assert.assertEquals(sFile.readBytes().joinedString, sFileInTarget.readBytes().joinedString)

        sFile.writeBytes(updatedData)
        Assert.assertEquals(updatedData.joinedString, sFile.readBytes().joinedString)
        Assert.assertNotEquals(initialData.joinedString, sFile.readBytes().joinedString)

        doSync()

        Assert.assertEquals(1, taskConfig.SOURCE_DIR.listFiles()!!.size)
        Assert.assertEquals(1, taskConfig.TARGET_DIR.listFiles()!!.size)

        Assert.assertTrue(sFileInTarget.exists())
        Assert.assertEquals(updatedData.joinedString, sFileInTarget.readBytes().joinedString)
        Assert.assertEquals(sFile.readBytes().joinedString, sFileInTarget.readBytes().joinedString)
    }
}