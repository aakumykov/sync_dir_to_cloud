package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.storage_dirs.downloadsDir
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class FileIsEmptyTest {

    /**
     * Каталог - не каталог.
     * Существует - не существует.
     * Пустой - непустой.
     * Читается - не читается.
     *
     * Тестировать по-новому, с учётом того, что теперь работает с файлами:
     *
     * А) файл:
     * - существует, непустой [not_empty_file_it_not_empty]
     * - существует, пустой [empty_file_is_empty]
     * - TODO: cуществует, но не читается []
     * - не существует [unexistent_file_throws_exception]
     * Б) каталог:
     * - существутет, непустой [not_empty_dir_is_not_empty]
     * - существует, пустой [empty_dir_is_empty]
     * - существует, не читается [unreadable_dir_throws_exception_on]
     * - не существует [unexistent_dir_throws_exception]
    */

    // Файл
    @Test
    fun not_empty_file_it_not_empty() {
        randomObjectInDownloads.also { file ->
            file.apply {
                Assert.assertTrue(createNewFile())
                writeBytes(randomBytes)
            }
            Assert.assertFalse(file.isEmpty)
        }
    }

    @Test
    fun empty_file_is_empty() {
        randomObjectInDownloads.also { file ->
            Assert.assertTrue(file.createNewFile())
            Assert.assertTrue(file.isEmpty)
        }
    }

    @Test
    fun unexistent_file_throws_exception() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            File(randomName).isEmpty
        }
    }


    @Test
    fun not_empty_dir_is_not_empty() {
        randomObjectInDownloads.also {
            Assert.assertTrue(it.mkdir())
            Assert.assertTrue(File(it, randomName).mkdir())
            Assert.assertTrue(File(it, randomName).createNewFile())
            Assert.assertFalse(it.isEmpty)
        }
    }

    @Test
    fun empty_dir_is_empty() {
        randomObjectInDownloads.also {
            Assert.assertTrue(it.mkdir())
            Assert.assertTrue(it.isEmpty)
        }
    }


    @Test
    fun unreadable_dir_throws_exception_on() {
        Assert.assertThrows(RuntimeException::class.java) {
            Environment.getDataDirectory().isEmpty
        }
    }

    @Test
    fun unexistent_dir_throws_exception() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            randomObjectInDownloads.isEmpty
        }
    }

    private val randomObjectInDownloads: File
        get() = File(downloadsDir, randomName)
}