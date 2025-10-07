package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomDeepDirName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File

class LocalFileHelperAuxMethodsTest : LocalFileHelperTestBase() {

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
}