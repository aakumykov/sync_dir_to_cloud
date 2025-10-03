package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import kotlin.random.Random

class LocalFileHelperContentsTest : LocalFileHelperTestBase() {

    companion object {
        const val DEEP_DIR_MIN_DEPTH = 2
        const val DEEP_DIR_MAX_DEPTH = 10
    }

    // TODO: негативное тестирование (НО НУЖНО ЛИ?)


    //
    // Вспомогательные методы
    //

    // Дая файлов

    @Test
    fun file_in_source() = run {
        val fileName = randomName
        val refFile = File(taskConfig.SOURCE_DIR, fileName)
        fileHelper.fileInSource(fileName).also {
            Assert.assertEquals(
                refFile.absolutePath,
                it.absolutePath
            )
        }
    }

    @Test
    fun file_in_target() = run {
        val fileName = randomName
        val refFile = File(taskConfig.TARGET_DIR, fileName)
        fileHelper.fileInTarget(fileName).also {
            Assert.assertEquals(
                refFile.absolutePath,
                it.absolutePath
            )
        }
    }

    @Test
    fun deep_file_in_source() = run {
        val fileName = randomName
        val dirName = randomDeepDirName
        val refFile = File(File(taskConfig.SOURCE_DIR,dirName), fileName)

        fileHelper.deepFileInSource(dirName, fileName).also {
            Assert.assertEquals(
                refFile.absolutePath,
                it.absolutePath
            )
        }
    }

    @Test
    fun deep_file_in_target() = run {
        val fileName = randomName
        val dirName = randomDeepDirName
        val refFile = File(File(taskConfig.TARGET_DIR,dirName), fileName)

        fileHelper.deepFileInTarget(dirName, fileName).also {
            Assert.assertEquals(
                refFile.absolutePath,
                it.absolutePath
            )
        }
    }


    // Для каталогов

    @Test
    fun dir_in_source() = run {
        val dirName = randomName
        val refDir = File(taskConfig.SOURCE_DIR, dirName)
        fileHelper.dirInSource(dirName).also {
            Assert.assertEquals(
                refDir.absolutePath,
                it.absolutePath
            )
        }
    }

    @Test
    fun dir_in_target() = run {
        val dirName = randomName
        val refDir = File(taskConfig.TARGET_DIR, dirName)
        fileHelper.dirInTarget(dirName).also {
            Assert.assertEquals(
                refDir.absolutePath,
                it.absolutePath
            )
        }
    }

    @Test
    fun deep_dir_in_source() = run {
        val childDirName = randomName
        val parentDirName = randomDeepDirName

        val parentDir = File(taskConfig.SOURCE_DIR, parentDirName)
        val refDir = File(parentDir, childDirName)

        fileHelper.deepDirInSource(parentDirName, childDirName).also {
            Assert.assertEquals(
                refDir.absolutePath,
                it.absolutePath
            )
        }
    }

    @Test
    fun deep_dir_in_target() = run {
        val childDirName = randomName
        val parentDirName = randomDeepDirName

        val parentDir = File(taskConfig.TARGET_DIR, parentDirName)
        val refDir = File(parentDir, childDirName)

        fileHelper.deepDirInTarget(parentDirName, childDirName).also {
            Assert.assertEquals(
                refDir.absolutePath,
                it.absolutePath
            )
        }
    }

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
        val dirName = randomName
        val dir = fileHelper.fileInSource(dirName)
        fileHelper.createDirInSource(dirName)
        Assert.assertTrue(dir.isDirectory)
        Assert.assertTrue(dir.exists())
    }

    @Test
    fun create_dir_in_target() = run {
        val dirName = randomName
        val dir = fileHelper.fileInTarget(dirName)
        fileHelper.createDirInTarget(dirName)
        Assert.assertTrue(dir.isDirectory)
        Assert.assertTrue(dir.exists())
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


    // Удаление неудаляемых файлов пока не проверяю...


}