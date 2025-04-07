package com.github.aakumykov.sync_dir_to_cloud.scenario.checks

import com.github.aakumykov.sync_dir_to_cloud.scenario.file.FileManipulationScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class SourceAndTargetFilesAreEquals : FileManipulationScenario() {
    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            val sourceFile = testFilesManager.sourceFile
            val targetFile = testFilesManager.sourceFile

            Assert.assertTrue(sourceFile.exists())
            Assert.assertTrue(targetFile.exists())

            Assert.assertEquals(
                testFilesManager.sourceFileContents.joinToString(""),
                testFilesManager.targetFileContents.joinToString(""),
            )
        }
}