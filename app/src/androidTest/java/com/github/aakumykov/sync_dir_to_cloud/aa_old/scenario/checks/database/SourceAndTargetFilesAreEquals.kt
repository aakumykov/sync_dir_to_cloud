package com.github.aakumykov.sync_dir_to_cloud.scenario.checks.file_checks.db_checks

import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class SourceAndTargetFilesAreEquals : FileScenario() {
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