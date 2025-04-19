package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalFileHelper
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomName
import org.junit.Assert
import org.junit.Test
import java.io.File


//
// Задача теста - убедиться, что методы FileHelper-а
// по созданию, удалению файлов/папок работают корректно.
//
class FileHelperTest : StorageAccessTestCase() {

    private val fileHelper = LocalFileHelper()


    @Test
    fun sourceFile1() = run {
        step("Предварительное удаление файла-1 в источнике") {
            fileHelper.deleteSourceFile1()
            Assert.assertFalse(fileHelper.sourceFile1Exists())
        }
        step("Создание файла-1 в источнике") {
            fileHelper.createSourceFile1()
            Assert.assertTrue(fileHelper.sourceFile1Exists())
        }
        step("Изменение файла-1 в источнике") {
            val oldContent = fileHelper.sourceFile1Content()
            fileHelper.modifySourceFile1()
            val newContent = fileHelper.sourceFile1Content()
            Assert.assertNotEquals(oldContent, newContent)
        }
        step("Удаление файла-1  в источнике") {
            fileHelper.deleteSourceFile1()
            Assert.assertFalse(fileHelper.sourceFile1Exists())
        }
    }


    @Test
    fun sourceFile2() = run {
        step("Предварительное удаление файла-2 в источнике") {
            fileHelper.deleteSourceFile2()
            Assert.assertFalse(fileHelper.sourceFile2Exists())
        }
        step("Создание файла-2 в источнике") {
            fileHelper.createSourceFile2()
            Assert.assertTrue(fileHelper.sourceFile2Exists())
        }
        step("Изменение файла-2 в источнике") {
            val oldContent = fileHelper.sourceFile2Content()
            fileHelper.modifySourceFile2()
            val newContent = fileHelper.sourceFile2Content()
            Assert.assertNotEquals(oldContent, newContent)
        }
        step("Удаление файла-2  в источнике") {
            fileHelper.deleteSourceFile2()
            Assert.assertFalse(fileHelper.sourceFile2Exists())
        }
    }


    @Test
    fun targetFile1() = run {
        step("Предварительное удаление файла-1 в приёмнике") {
            fileHelper.deleteTargetFile1()
            Assert.assertFalse(fileHelper.targetFile1Exists())
        }
        step("Создание файла-1 в приёмнике") {
            fileHelper.createTargetFile1()
            Assert.assertTrue(fileHelper.targetFile1Exists())
        }
        step("Изменение файла-1 в приёмнике") {
            val oldContent = fileHelper.targetFile1Content()
            fileHelper.modifyTargetFile1()
            val newContent = fileHelper.targetFile1Content()
            Assert.assertNotEquals(oldContent, newContent)
        }
        step("Удаление файла-1  в приёмнике") {
            fileHelper.deleteTargetFile1()
            Assert.assertFalse(fileHelper.targetFile1Exists())
        }
    }


    @Test
    fun targetFile2() = run {
        step("Предварительное удаление файла-2 в приёмнике") {
            fileHelper.deleteTargetFile2()
            Assert.assertFalse(fileHelper.targetFile2Exists())
        }
        step("Создание файла-2 в приёмнике") {
            fileHelper.createTargetFile2()
            Assert.assertTrue(fileHelper.targetFile2Exists())
        }
        step("Изменение файла-2 в приёмнике") {
            val oldContent = fileHelper.targetFile2Content()
            fileHelper.modifyTargetFile2()
            val newContent = fileHelper.targetFile2Content()
            Assert.assertNotEquals(oldContent, newContent)
        }
        step("Удаление файла-2  в приёмнике") {
            fileHelper.deleteTargetFile2()
            Assert.assertFalse(fileHelper.targetFile2Exists())
        }
    }


    @Test
    fun deleteFilesInDir() = run {
        step("Создание тестового каталога с содержимым и удаление этого содержимого") {

            val dir0 = File(device.targetContext.cacheDir, randomName)

            step("Создание начального каталога ('${dir0.absolutePath}')") {
                dir0.mkdirs()
                Assert.assertTrue(dir0.exists())

                val file1 = File(dir0, randomName)
                step("Создание файла в начальном каталоге ('${file1.absolutePath}')") {
                    file1.writeBytes(randomBytes(10))
                    Assert.assertTrue(file1.exists())
                }

                val dir1 = File(dir0, randomName)
                step("Создание подкаталога ('${dir1.absolutePath}')") {
                    dir1.mkdir()
                    Assert.assertTrue(dir1.exists())

                    val file2 = File(dir1, randomName)
                    step("Создание файла в подкаталоге ('${file2.absolutePath}')") {
                        file2.writeBytes(randomBytes(5))
                        Assert.assertTrue(file2.exists())
                    }
                }

                step("Удаление содержимого начального каталога ('${dir0.absolutePath}')") {
                    fileHelper.deleteAllFilesInDir(dir0)
                    Assert.assertEquals(0, dir0.list()?.size ?: -1)
                }
            }
        }
    }
}