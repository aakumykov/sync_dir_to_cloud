package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.file.deletion

import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class DeleteTargetFile(fileName: String) : FileScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Удаление из приёмника файла '$fileName'") {
            testFilesManager.deleteFileFromTarget(fileName).also {
                Assert.assertFalse(it.exists())
            }
        }
    }
}
