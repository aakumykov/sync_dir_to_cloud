package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.byte_array_joined_string.joinedString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ModificationInCacheDirSimpleTest {

    private val cacheDir: File
        get() = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir

    val randomName: String
        get() = UUID.randomUUID().toString()

    val randomBytes1024: ByteArray
        get() = Random.nextBytes(1024)

    fun randomBytes(amount: Int): ByteArray = Random.nextBytes(amount)


    @Test
    fun source_file_modification() {
        repeat(1000) {

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
            sourceFile.apply {
                Assert.assertTrue(createNewFile())
                Assert.assertTrue(sourceFile.exists())

                writeBytes(data)
                // Проверяю, что в файл записались именно данные [data].
                Assert.assertEquals(data.joinedString, this.readBytes().joinedString)
            }

            // Пишу в файл новые данные.
            sourceFile.writeBytes(newData)
            // Проверяю, что оне корректно записались.
            Assert.assertEquals(newData.joinedString, sourceFile.readBytes().joinedString)
        }
    }
}