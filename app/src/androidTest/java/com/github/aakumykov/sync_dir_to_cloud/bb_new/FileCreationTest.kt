package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileManagerHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.LocalTestFileManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Проверяет корректность работы методов [LocalTestFileManager]-а
 */
class FileCreationTest : StorageAccessTestCase() {

    private val fileManager = LocalFileManagerHolder.fileManager
    private val fileConfig = LocalFileCofnig

    /*@Test
    fun when_sync_one_file_in_source_then_that_file_appears_in_tartet() = run {
        scenario(CreateOneSourceFileScenario())
    }*/


    @Test
    fun createFileInSource() = run {
        step("Создание файла в источнике") {

            step("Предварительное удаление файла в источнике") {
                fileManager.deleteSourceFile1()
                Assert.assertFalse(fileManager.sourceFile1Exists())
            }
            step("Создание файла в источнике") {
                fileManager.createSourceFile1()
                Assert.assertTrue(fileManager.sourceFile1Exists())
            }
        }
    }

    @Test
    fun createFileInTarget() = run {
        step("Создание файла в приёмнике") {

            step("Предварительное удаление файла в приёмнике") {
                fileManager.deleteTargetFile1()
                Assert.assertFalse(fileManager.targetFile1Exists())
            }
            step("Создание файла в приёмнике") {
                fileManager.createTargetFile1()
                Assert.assertTrue(fileManager.targetFile1Exists())
            }
        }
    }
}