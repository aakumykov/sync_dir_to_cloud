package com.github.aakumykov.sync_dir_to_cloud.scenario.checks.file_checks.db_checks

import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class SourceAndTargetFilesAreEquals : FileScenario() {
    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            val sourceFile = testFileManager.sourceFile
            val targetFile = testFileManager.sourceFile

            Assert.assertTrue(sourceFile.exists())
            Assert.assertTrue(targetFile.exists())

            Assert.assertEquals(
                testFileManager.sourceFileContents.joinToString(""),
                testFileManager.targetFileContents.joinToString(""),
            )
        }
}