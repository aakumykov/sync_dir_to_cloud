package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.extensions

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FileDirIsEmptyTest {

/**
 * Каталог - не каталог.
 * Существует - не существует.
 * Пустой - непустой.
 * Читается - не читается.
 *
 * Тестировать по-новому, с учётом того, что теперь работает с файлами.
*/

    @Test
    fun existing_empty_dir_is_empty() {
        randomObjectInDownloads.also {
            Assert.assertTrue(it.mkdir())
            Assert.assertTrue(it.dirIsEmpty)
        }
    }

    @Test
    fun unexistent_dir_throws_exception() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            randomObjectInDownloads.dirIsEmpty
        }
    }

    @Test
    fun file_throws_exception() {
        randomObjectInDownloads.also {
            Assert.assertTrue(it.createNewFile())
            Assert.assertThrows(IllegalArgumentException::class.java) { it.dirIsEmpty }
        }
    }

    @Test
    fun not_empty_dir_is_not_empty() {
        randomObjectInDownloads.also {
            Assert.assertTrue(it.mkdir())
            Assert.assertTrue(File(it, randomName).createNewFile())
            Assert.assertTrue(File(it, randomName).mkdir())
            Assert.assertFalse(it.dirIsEmpty)
        }
    }

    @Test
    fun throws_exception_on_unreadable_dir() {
        Assert.assertThrows(RuntimeException::class.java) {
            Environment.getDataDirectory().dirIsEmpty
        }
    }

    private val downloadsDir: File
        get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    private val randomObjectInDownloads: File
        get() = File(downloadsDir, randomName)
}