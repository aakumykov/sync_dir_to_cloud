package com.github.aakumykov.sync_dir_to_cloud.scenario

import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesManager
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class DeleteSourceFile(fileName: String) : Scenario() {

    private val testFileManager by lazy { TestFilesManager() }

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Удаление из источника файла '$fileName'") {
            testFileManager.deleteFileFromSource(fileName).also {
                Assert.assertFalse(it.exists())
            }
        }
    }
}
