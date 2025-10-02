package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File
import kotlin.random.Random

class LocalFileHelperContentsTest : LocalFileHelperTestBase() {

    private val randomSize get() = Random.nextInt(1,101)

    // TODO: негативное тестирование

    //
    // Создание файла
    //

    /**
     * [LocalFileHelper.createFileInSource]
     */
    @Test
    fun create_file_in_source() = run {
        fileHelper.createSourceDir()
        fileHelper.createFileInSource(fileConfig.FILE_1_NAME).also {
            Assert.assertTrue(it.exists())
        }
    }

    /**
     * [LocalFileHelper.createFileInTarget]
     */
    @Test
    fun create_file_in_target() = run {
        fileHelper.createTargetDir()
        fileHelper.createFileInTarget(fileConfig.FILE_1_NAME).also {
            Assert.assertTrue(it.exists())
        }
    }

    /**
     * [LocalFileHelper.createFileOfSize]
     */
    @Test
    fun create_file_with_custom_size_in_source() = run {
        val size = randomSize
        val file = fileHelper.fileInSource(fileConfig.FILE_1_NAME)
        fileHelper.createSourceDir()
        fileHelper.createFileOfSize(file, size).also {
            Assert.assertTrue(it.exists())
            Assert.assertEquals(size, it.length().toInt())
        }
    }

    /**
     * [LocalFileHelper.createFileOfSize]
     */
    @Test
    fun create_file_with_custom_size_in_target() = run {
        val size = randomSize
        val file = fileHelper.fileInTarget(fileConfig.FILE_1_NAME)
        fileHelper.createTargetDir()
        fileHelper.createFileOfSize(file, size).also {
            Assert.assertTrue(it.exists())
            Assert.assertEquals(size, it.length().toInt())
        }
    }

    /**
     * [LocalFileHelper.createFileOfSize]
     */
    @Test
    fun create_file_with_custom_size_in_source_deep_dir() = run {
        val deepDirName = "1/2/3"
        val deepDir = fileHelper.dirInSource(deepDirName)

        fileHelper.createSourceDir()
        fileHelper.createDirInSource(deepDirName)

        val file = File(deepDir, randomName)
        val size = randomSize

        fileHelper.createFileOfSize(file, size)
        Assert.assertTrue(file.exists())
        Assert.assertEquals(size.toLong(), file.length())
    }

    /**
     * [LocalFileHelper.createFileOfSize]
     */
    @Test
    fun create_file_with_custom_size_in_target_deep_dir() = run {
        val deepDirName = "1/2/3"
        val deepDir = fileHelper.dirInTarget(deepDirName)

        fileHelper.createTargetDir()
        fileHelper.createDirInTarget(deepDirName)

        val file = File(deepDir, randomName)
        val size = randomSize

        fileHelper.createFileOfSize(file, size)
        Assert.assertTrue(file.exists())
        Assert.assertEquals(size.toLong(), file.length())
    }






    /**
     * [LocalFileHelper.deleteAllFilesInDir]
     */
    @Test
    fun delete_all_files_in_deep_dir() = run {

        val deepDirName = "1/2/3"
        val deepDir = fileHelper.dirInTarget(deepDirName)
        deepDir.mkdirs()
        Assert.assertTrue(deepDir.exists())

        val nestedFile = File(deepDir, "file1.txt")
        fileHelper.createFileOfSize(nestedFile)
        Assert.assertTrue(nestedFile.exists())

        fileHelper.deleteAllFilesInDir(deepDir)

        Assert.assertFalse(nestedFile.exists())
        Assert.assertTrue(deepDir.exists())
        Assert.assertEquals(0, fileHelper.listDir(deepDir).size)
    }

}