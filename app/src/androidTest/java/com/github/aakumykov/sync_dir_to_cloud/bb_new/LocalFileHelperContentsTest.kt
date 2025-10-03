package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomBytes
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
    private val randomSize get() = Random.nextInt(1,101)

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
    fun throws_exception_on_read_unexistent_file_from_target() = run {
        val fileName = randomName
        val file = fileHelper.createFileInTarget(fileName).apply {
            delete()
            Assert.assertFalse(this.exists())
        }
        Assert.assertThrows(IOException::class.java) {
            fileHelper.getFileContents(file)
        }
    }




    /**
     * [LocalFileHelper.deleteAllFilesInDir]
     */
    // TODO


    private fun randomDeepDirName(minDepth: Int = DEEP_DIR_MIN_DEPTH, maxDepth: Int = DEEP_DIR_MAX_DEPTH): String {
        return buildList {
            repeat(Random.nextInt(minDepth, maxDepth+1)) {
                add(randomName)
            }
        }.joinToString(CloudWriter.DS)
    }

    private val randomDeepDirName: String = randomDeepDirName()
}