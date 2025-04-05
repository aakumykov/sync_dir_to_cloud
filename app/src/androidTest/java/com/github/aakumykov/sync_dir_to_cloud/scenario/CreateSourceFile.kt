package com.github.aakumykov.sync_dir_to_cloud.scenario

import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesManager
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class CreateSourceFile(
    file1Name: String,
    file1Size: Int
) : FileCreatingScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание тестового файла '$file1Name' размером $file1Size в источнике") {
            runTest {
                testFilesManager.createFileInSource(file1Name, file1Size).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(file1Size, it.length().toInt())
                }
            }
        }
    }
}


class CreateTargetFile(
    file1Name: String,
    file1Size: Int
) : FileCreatingScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание тестового файла '$file1Name' размером $file1Size в приёмнике") {
            runTest {
                testFilesManager.createFileInTarget(file1Name, file1Size).also {
                    Assert.assertTrue(it.exists())
                    Assert.assertEquals(file1Size, it.length().toInt())
                }
            }
        }
    }
}

abstract class FileCreatingScenario() : Scenario() {
    protected val testFilesManager by lazy { TestFilesManager(TestTaskConfig) }
}