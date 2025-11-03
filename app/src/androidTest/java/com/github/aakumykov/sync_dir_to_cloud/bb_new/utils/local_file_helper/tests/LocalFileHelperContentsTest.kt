package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.systemRootDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path.FilePathSamples.ROOT_PATH
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException

class LocalFileHelperContentsTest : LocalFileHelperTestBase() {

    companion object {
        const val READ_ONLY_FILE = "/proc/cpuinfo"

        const val READ_ONLY_DEEP_DIR = "proc"
        const val READ_ONLY_DEEP_FILE_NAME = "cpuinfo"
    }

    // TODO: негативное тестирование (НО НУЖНО ЛИ?)

    private val rootFileHelper = LocalFileHelper(
        LocalToLocalSyncNoBackupTaskConfig(
            SOURCE_DIR = systemRootDir,
            TARGET_DIR = systemRootDir
        )
    )


    //
    // Создание файла
    //

    // Простого файла с заданным содержимым

    @Test
    fun create_file_with_data_in_source() = run {
        val data = randomBytes
        fileHelper.createSourceDir()
        fileHelper.createFileInSource(randomName, data).also {
            Assert.assertTrue(it.exists())
            Assert.assertEquals(data.size.toLong(), it.length())
            Assert.assertEquals(
                data.joinToString(),
                it.readBytes().joinToString()
            )
        }
    }


    @Test
    fun create_file_with_data_in_target() = run {
        val data = randomBytes
        fileHelper.createTargetDir()
        fileHelper.createFileInTarget(randomName, data).also {
            Assert.assertTrue(it.exists())
            Assert.assertEquals(data.size.toLong(), it.length())
            Assert.assertEquals(
                data.joinToString(),
                it.readBytes().joinToString()
            )
        }
    }


    // Глубокого файла с заданным содержимым

    @Test
    fun create_deep_file_with_data_in_source() = run {
        val deepDirName = randomDeepDirName
        val fileName = randomName
        val data = randomBytes

        fileHelper.createSourceDir()

        fileHelper.createDeepFileInSource(deepDirName, fileName, data).also {
            Assert.assertTrue(it.exists())
            Assert.assertEquals(
                data.joinToString(),
                it.readBytes().joinToString()
            )
        }
    }


    @Test
    fun create_deep_file_with_data_in_target() = run {
        val deepDirName = randomDeepDirName
        val fileName = randomName
        val data = randomBytes

        fileHelper.createSourceDir()

        fileHelper.createDeepFileInTarget(deepDirName, fileName, data).also {
            Assert.assertTrue(it.exists())
            Assert.assertEquals(
                data.joinToString(),
                it.readBytes().joinToString()
            )
        }
    }


    //
    // Создание каталога
    //

    // Простого каталога

    @Test
    fun create_dir_in_source() = run {
        fileHelper.createDirInSource(randomName).also {
            Assert.assertTrue(it.exists())
            Assert.assertTrue(it.isDirectory)
            Assert.assertEquals(0, it.list()?.size)
        }
    }

    @Test
    fun create_dir_in_target() = run {
        fileHelper.createDirInTarget(randomName).also {
            Assert.assertTrue(it.exists())
            Assert.assertTrue(it.isDirectory)
            Assert.assertEquals(0, it.list()?.size)
        }
    }



    // Глубокого каталога

    /**
     * [LocalFileHelper.createDirInSource]
     */
    @Test
    fun create_deep_dir_in_source() = run {
        val deepDirName = randomDeepDirName()
        fileHelper.createDirInSource(deepDirName).also {
            Assert.assertTrue(it.exists())
        }
    }

    /**
     * [LocalFileHelper.createDirInTarget]
     */
    @Test
    fun create_deep_dir_in_target() = run {
        val deepDirName = randomDeepDirName()
        fileHelper.createDirInTarget(deepDirName).also {
            Assert.assertTrue(it.exists())
        }
    }


    //
    // Чтение
    //

    // Файлов

    /**
     * [LocalFileHelper.getFileContents]
     */

    @Test
    fun read_file_contents_from_source() = run {
        fileHelper.createSourceDir()
        val data = randomBytes
        fileHelper.createFileInSource(randomName, data).also {
            Assert.assertEquals(
                data.joinToString(),
                fileHelper.getFileContents(it).joinToString()
            )
        }
    }

    @Test
    fun read_file_contents_from_target() = run {
        fileHelper.createTargetDir()
        val data = randomBytes
        fileHelper.createFileInTarget(randomName, data).also {
            Assert.assertEquals(
                data.joinToString(),
                fileHelper.getFileContents(it).joinToString()
            )
        }
    }


    @Test
    fun throws_exception_on_read_unexistent_file_from_source() = run {

        fileHelper.createSourceDir()

        val file = fileHelper.createFileInSource(randomName).apply {
            Assert.assertTrue(this.exists())
            delete()
            Assert.assertFalse(this.exists())
        }

        Assert.assertThrows(IOException::class.java) {
            fileHelper.getFileContents(file)
        }
    }

    @Test
    fun throws_exception_on_read_unexistent_file_from_target() = run {

        fileHelper.createTargetDir()

        val file = fileHelper.createFileInTarget(randomName).apply {
            Assert.assertTrue(this.exists())
            delete()
            Assert.assertFalse(this.exists())
        }

        Assert.assertThrows(IOException::class.java) {
            fileHelper.getFileContents(file)
        }
    }


    // Каталогов

    /**
     * [LocalFileHelper.listDirInSource]
     * [LocalFileHelper.listDirInTarget]
     */

    @Test
    fun list_empty_dir_in_source() = run {
        val deepDirName = randomDeepDirName

        fileHelper.createSourceDir()

        fileHelper.createDirInSource(deepDirName).also {
            Assert.assertTrue(it.exists())
        }

        fileHelper.listDirInSource(deepDirName).also { list ->
            Assert.assertTrue(list.isEmpty())
            Assert.assertEquals(0, list.size)
        }
    }

    @Test
    fun list_empty_dir_in_target() = run {
        val deepDirName = randomDeepDirName

        fileHelper.createTargetDir()

        fileHelper.createDirInTarget(deepDirName).also {
            Assert.assertTrue(it.exists())
        }

        fileHelper.listDirInTarget(deepDirName).also { list ->
            Assert.assertTrue(list.isEmpty())
            Assert.assertEquals(0, list.size)
        }
    }


    @Test
    fun list_non_empty_dir_in_source() = run {
        val deepDirName = randomDeepDirName

        fileHelper.createDeepFileInSource(deepDirName, randomName).also {
            Assert.assertTrue(it.exists())
        }

        fileHelper.listDirInSource(deepDirName).also {
            Assert.assertEquals(1, it.size)
        }
    }

    @Test
    fun list_non_empty_dir_in_target() = run {
        val deepDirName = randomDeepDirName

        fileHelper.createDeepFileInTarget(deepDirName, randomName).also {
            Assert.assertTrue(it.exists())
        }

        fileHelper.listDirInTarget(deepDirName).also {
            Assert.assertEquals(1, it.size)
        }
    }


    @Test
    fun throws_exception_listing_unexistent_dir_in_source() = run {
        Assert.assertThrows(RuntimeException::class.java) {
            fileHelper.listDirInSource(randomDeepDirName())
        }
    }

    @Test
    fun throws_exception_listing_unexistent_dir_in_target() = run {
        Assert.assertThrows(RuntimeException::class.java) {
            fileHelper.listDirInTarget(randomDeepDirName())
        }
    }


    // Подсчёт файлов в источнике

    @Test
    fun counting_dir_with_files_in_source_returns_positive_number() = run {
        val dirName = randomName
        fileHelper.createDirInSource(dirName).apply {
            File(this, randomName).createNewFile()
            File(this, randomName).createNewFile()
        }
        Assert.assertEquals(2, fileHelper.dirInSourceItemsCount(dirName))
    }

    @Test
    fun counting_empty_dir_in_source_returns_zero() = run {
        fileHelper.createDirInSource(randomName).also {
            Assert.assertEquals(0, fileHelper.dirInSourceItemsCount(it.name))
        }
    }

    @Test
    fun counting_contents_of_unexistent_dir_in_source_throws_exception() = run {
        Assert.assertThrows(RuntimeException::class.java) {
            fileHelper.dirInSourceItemsCount(randomName)
        }
    }

    @Test
    fun counting_unreadable_dir_in_source_throws_exception() = run {
        Assert.assertThrows(RuntimeException::class.java) {
            android.os.Environment.getDataDirectory().also {
                // it == File("/data")
                rootFileHelper.dirInSourceItemsCount(it.name)
            }
        }
    }


    // Подсчёт файлов в приёмнике

    @Test
    fun counting_dir_with_files_in_target_returns_positive_number() = run {
        val dirName = randomName
        fileHelper.createDirInTarget(dirName).apply {
            File(this,randomName).createNewFile()
            File(this,randomName).createNewFile()
        }
        Assert.assertEquals(2, fileHelper.dirInTargetItemsCount(dirName))
    }

    @Test
    fun counting_empty_dir_in_target_returns_zero() = run {
        fileHelper.createDirInTarget(randomName).also {
            Assert.assertEquals(0, fileHelper.dirInTargetItemsCount(it.name))
        }
    }

    @Test
    fun counting_contents_of_unexistent_dir_in_target_throws_exception() = run {
        Assert.assertThrows(RuntimeException::class.java) {
            fileHelper.dirInTargetItemsCount(randomName)
        }
    }

    @Test
    fun counting_unreadable_dir_in_target_throws_exception() = run {
        Assert.assertThrows(RuntimeException::class.java) {
            android.os.Environment.getDataDirectory().also {
                // it == File("/data")
                rootFileHelper.dirInTargetItemsCount(it.name)
            }
        }
    }



    // Обновление файлов

    /**
     * [LocalFileHelper.modifyFileInSource]
     * [LocalFileHelper.modifyFileInTarget]
     */

    @Test
    fun modify_file_in_source() = run {
        val fileName = randomName
        val dirName = randomDeepDirName
        val data = randomBytes

        fileHelper.createSourceDir().also { Assert.assertTrue(it) }

        fileHelper.createDeepFileInSource(dirName, fileName, data).also {
            Assert.assertTrue(it.exists()) }

        fileHelper.modifyFileInSource(dirName, fileName).also {
            Assert.assertTrue(it.exists())
            Assert.assertNotEquals(
                data.joinToString(),
                it.readBytes().joinToString()
            )
        }
    }

    @Test
    fun modify_file_in_target() = run {
        val fileName = randomName
        val dirName = randomDeepDirName
        val data = randomBytes

        fileHelper.createTargetDir().also { Assert.assertTrue(it) }

        fileHelper.createDeepFileInTarget(dirName, fileName, data).also {
            Assert.assertTrue(it.exists()) }

        fileHelper.modifyFileInTarget(dirName, fileName).also {
            Assert.assertTrue(it.exists())
            Assert.assertNotEquals(
                data.joinToString(),
                it.readBytes().joinToString()
            )
        }
    }


    //
    // Удаление
    //

    // Файлов

    @Test
    fun delete_simple_file_from_source() = run {
        val fileName = randomName

        fileHelper.createSourceDir()

        fileHelper.createFileInSource(fileName).also {
            Assert.assertTrue(it.exists()) }

        fileHelper.deleteFileFromSource(fileName).also {
            Assert.assertFalse(it.exists()) }
    }

    @Test
    fun delete_simple_file_from_target() = run {
        val fileName = randomName

        fileHelper.createTargetDir()

        fileHelper.createFileInTarget(fileName).also {
            Assert.assertTrue(it.exists()) }

        fileHelper.deleteFileFromTarget(fileName).also {
            Assert.assertFalse(it.exists()) }
    }


    @Test
    fun delete_deep_file_from_source() = run {
        val fileName = randomName
        val deepDirName = randomDeepDirName

        fileHelper.createSourceDir()

        fileHelper.createDeepFileInSource(deepDirName, fileName).also {
            Assert.assertTrue(it.exists()) }

        fileHelper.deleteDeepFileFromSource(deepDirName, fileName).also {
            Assert.assertFalse(it.exists()) }
    }

    @Test
    fun delete_deep_file_from_target() = run {
        val fileName = randomName
        val deepDirName = randomDeepDirName

        fileHelper.createTargetDir()

        fileHelper.createDeepFileInTarget(deepDirName, fileName).also {
            Assert.assertTrue(it.exists()) }

        fileHelper.deleteDeepFileFromTarget(deepDirName, fileName).also {
            Assert.assertFalse(it.exists()) }
    }


    @Test
    fun throws_exception_on_delete_simple_file_from_source() = run {
        Assert.assertThrows(IOException::class.java) {
            fileHelper.deleteFileFromSource(randomName)
        }
    }

    @Test
    fun throws_exception_on_delete_simple_file_from_target() = run {
        Assert.assertThrows(IOException::class.java) {
            fileHelper.deleteFileFromTarget(randomName)
        }
    }


    @Test
    fun throws_exception_on_delete_deep_file_from_source() = run {
        Assert.assertThrows(IOException::class.java) {
            fileHelper.deleteDeepFileFromSource(randomDeepDirName, randomName)
        }
    }

    @Test
    fun throws_exception_on_delete_deep_file_from_target() = run {
        Assert.assertThrows(IOException::class.java) {
            fileHelper.deleteDeepFileFromTarget(randomDeepDirName, randomName)
        }
    }


    @Test
    fun throws_exception_deleting_read_only_file() = run {

        Assert.assertThrows(IOException::class.java) {
            rootFileHelper.deleteFileFromSource(READ_ONLY_FILE)
        }

        Assert.assertThrows(IOException::class.java) {
            rootFileHelper.deleteFileFromTarget(READ_ONLY_FILE)
        }
    }

    @Test
    fun throws_exception_deleting_deep_read_only_file() = run {

        Assert.assertThrows(IOException::class.java) {
            rootFileHelper.deleteDeepFileFromSource(READ_ONLY_DEEP_DIR, READ_ONLY_DEEP_FILE_NAME)
        }

        Assert.assertThrows(IOException::class.java) {
            rootFileHelper.deleteDeepFileFromTarget(READ_ONLY_DEEP_DIR, READ_ONLY_DEEP_FILE_NAME)
        }
    }


    // Каталогов

    @Test
    fun delete_dir_from_source() = run {
        fileHelper.createSourceDir()
        val dirName = randomName
        fileHelper.createDirInSource(dirName).also { Assert.assertTrue(it.exists()) }
        fileHelper.deleteDirFromSource(dirName).also { Assert.assertFalse(it.exists()) }
    }

    @Test
    fun delete_dir_from_target() = run {
        fileHelper.createTargetDir()
        val dirName = randomName
        fileHelper.createDirInTarget(dirName).also { Assert.assertTrue(it.exists()) }
        fileHelper.deleteDirFromTarget(dirName).also { Assert.assertFalse(it.exists()) }
    }

    @Test
    fun throws_exception_deleting_unexistent_dir() = run {
        Assert.assertThrows(IOException::class.java) {
            fileHelper.deleteDirFromSource(randomName)
        }
        Assert.assertThrows(IOException::class.java) {
            fileHelper.deleteDirFromTarget(randomName)
        }
    }


    @Test
    fun throws_exception_deleting_read_only_dir() = run {
        Assert.assertThrows(IOException::class.java) {
            rootFileHelper.deleteDirFromSource(READ_ONLY_DEEP_DIR)
        }
        Assert.assertThrows(IOException::class.java) {
            rootFileHelper.deleteDirFromTarget(READ_ONLY_DEEP_DIR)
        }
    }



}