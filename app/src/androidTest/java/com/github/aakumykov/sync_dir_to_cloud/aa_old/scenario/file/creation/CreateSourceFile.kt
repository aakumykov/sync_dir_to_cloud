package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.file.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class CreateSourceFile(fileConfig: FileConfig) : FileScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        val fileName = fileConfig.FILE_1_NAME
        val fileSize = fileConfig.FILE_1_SIZE

        step("Создание тестового файла '$fileName' размером $fileSize в источнике") {
            runTest {
                testFilesManager.createFileInSource(fileName, fileSize).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(fileSize, it.length().toInt())
                }
            }
        }
    }
}
