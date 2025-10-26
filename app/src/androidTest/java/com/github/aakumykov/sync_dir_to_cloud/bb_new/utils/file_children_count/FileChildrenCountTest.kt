package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.cache_dir.cacheDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import okio.FileNotFoundException
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException

class FileChildrenCountTest : StorageAccessTestCase() {

    /**
     * - пустой каталог [empty_dir_returns_zero]
     * - непустой каталог [non_empty_dir_returns_positive_number]
     * - несуществующий каталог приводит к исключению [unexistent_dir_throws_exception]
     * - нечитаемый каталог приводит к исключению [unreadable_dir_throws_exception]
     */

    private val newTempDir: File get() {
        return File(cacheDir, randomName).apply {
            Assert.assertTrue(mkdir())
        }
    }


    @Test
    fun temp_dir_is_dir() {
        Assert.assertTrue(newTempDir.isDirectory)
    }


    @Test
    fun empty_dir_returns_zero() {
        Assert.assertEquals(0, newTempDir.childrenCount)
    }


    @Test
    fun non_empty_dir_returns_positive_number() {
        val parentDir = newTempDir
        Assert.assertTrue(File(parentDir, randomName).mkdir())
        Assert.assertTrue(parentDir.childrenCount > 0)
    }


    @Test
    fun unexistent_dir_throws_exception() {
        Assert.assertThrows(NullPointerException::class.java) {
            File(randomName).childrenCount
        }
    }


    @Test
    fun unreadable_dir_throws_exception() {
        Assert.assertThrows(NullPointerException::class.java) {
            File("/data").childrenCount
        }
    }
}