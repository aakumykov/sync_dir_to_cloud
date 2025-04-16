package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import org.junit.Assert
import org.junit.Test


//
// Задача теста - убедиться, что методы FileHelper-а
// по созданию, удалению файлов/папок работают корректно.
//
class FileHelperTest : StorageAccessTestCase() {

    private val fileHelper = LocalFileHelperHolder.fileHelper


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
}