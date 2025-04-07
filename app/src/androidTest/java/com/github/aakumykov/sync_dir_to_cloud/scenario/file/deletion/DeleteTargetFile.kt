package com.github.aakumykov.sync_dir_to_cloud.scenario.file.deletion

import com.github.aakumykov.sync_dir_to_cloud.scenario.file.FileManipulationScenario
import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesManager
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class DeleteTargetFile(fileName: String) : FileManipulationScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Удаление из приёмника файла '$fileName'") {
            testFilesManager.deleteFileFromTarget(fileName).also {
                Assert.assertFalse(it.exists())
            }
        }
    }
}
