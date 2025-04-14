package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert
import java.io.File

class ReCreateTargetDir : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Удаление старого и создание нового каталога приёмника") {
            File(LocalTaskConfig.TARGET_PATH).also { targetDir: File ->
                targetDir.deleteRecursively()
                Assert.assertFalse(targetDir.exists())

                targetDir.mkdir()
                Assert.assertTrue(targetDir.exists())
            }
        }
    }
}