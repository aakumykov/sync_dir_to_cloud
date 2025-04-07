package com.github.aakumykov.sync_dir_to_cloud.scenario.file.creation

import com.github.aakumykov.sync_dir_to_cloud.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.scenario.file.FileManipulationScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class CreateTargetFile(fileConfig: FileConfig) : FileManipulationScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        val fileName = fileConfig.FILE_1_NAME
        val fileSize = fileConfig.FILE_1_SIZE

        step("Создание тестового файла '$fileName' размером $fileSize в приёмнике") {
            runTest {
                testFilesManager.createFileInTarget(fileName, fileSize).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(fileSize, it.length().toInt())
                }
            }
        }
    }
}