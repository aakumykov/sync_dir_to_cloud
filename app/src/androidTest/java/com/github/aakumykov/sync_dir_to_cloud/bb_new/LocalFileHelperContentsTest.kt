package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File
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

    // Файлы

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


    // Каталоги

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



    // Создание простого каталога

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



    // Создание глубокого каталога

    @Test
    fun create_deep_dir_in_source() = run {
        val deepDirName = randomDeepDirName()
        fileHelper.createDirInSource(deepDirName).also {
            Assert.assertTrue(it.exists())
        }
    }

    @Test
    fun create_deep_dir_in_target() = run {
        val deepDirName = randomDeepDirName()
        fileHelper.createDirInTarget(deepDirName).also {
            Assert.assertTrue(it.exists())
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