package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v0_simplest

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.byte_array_joined_string.joinedString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
import kotlin.random.Random

/**
 * Полностью ручная проба создания, изменения и копирования
 * файловых данных после изменения.
 */
@RunWith(AndroidJUnit4::class)
class FileModificationInCacheDir {

    private val cacheDir: File
        get() = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir

    val randomName: String
        get() = UUID.randomUUID().toString()

    val randomBytes1024: ByteArray
        get() = Random.Default.nextBytes(1024)

    fun randomBytes(amount: Int): ByteArray = Random.Default.nextBytes(amount)


    @Test
    fun file_modification() {
        repeat(5) {

            // === Проверка создания и изменения "исходного" файла ===

            val sourceFile = File(cacheDir, randomName)
            // Проверяю, что файла ещё нет. Зачем? Для большей надёжности.
            Assert.assertFalse(sourceFile.exists())

            val data = randomBytes1024
            val newData = randomBytes1024
            // Проверяю, что данные непустые.
            Assert.assertTrue(data.isNotEmpty())
            Assert.assertTrue(newData.isNotEmpty())
            // Проверяю, что исходные и новые данные отличаются.
            Assert.assertNotEquals(data.joinedString, newData.joinedString)

            // Создаю файл и пишу в него исходные данные.
            Assert.assertTrue(sourceFile.createNewFile())
            Assert.assertTrue(sourceFile.exists())
            Assert.assertTrue(sourceFile.readBytes().isEmpty())

            sourceFile.writeBytes(data)
            // Проверяю, что в файл записались именно данные [data].
            Assert.assertEquals(data.joinedString, sourceFile.readBytes().joinedString)

            // Пишу в файл новые данные.
            sourceFile.writeBytes(newData)
            // Проверяю, что оне корректно записались.
            Assert.assertEquals(newData.joinedString, sourceFile.readBytes().joinedString)



            // === Проверка копирования "исходного" файла в другой файл ===

            val targetFile = File(cacheDir, randomName)
            // Проверяю, что файл назначения отсутствует.
            Assert.assertFalse(targetFile.exists())

            // Создаю файл и пишу в него исходные данные.
            Assert.assertTrue(targetFile.createNewFile())
            Assert.assertTrue(targetFile.exists())
            Assert.assertTrue(targetFile.readBytes().isEmpty())

            targetFile.writeBytes(data)
            // Проверяю, что в файл записались именно данные [data].
            Assert.assertEquals(data.joinedString, targetFile.readBytes().joinedString)


            sourceFile.copyTo(targetFile, true).also {
                Assert.assertEquals(targetFile, it)
            }
            // Проверяю, что в новый файл записались [newData]
            Assert.assertEquals(newData.joinedString, targetFile.readBytes().joinedString)


            // Повторяю изменение исходного и копирование в другой.
            val newData2 = randomBytes1024

            sourceFile.writeBytes(newData2)
            Assert.assertEquals(newData2.joinedString, sourceFile.readBytes().joinedString)

            sourceFile.copyTo(targetFile, true).also {
                Assert.assertEquals(targetFile, it)
            }
        }
    }
}