package com.github.aakumykov.sync_dir_to_cloud.scenario.dir

import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert
import java.io.File

class CreateTargetDir(private val taskConfig: LocalTaskConfig) : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            step("Создание каталога назначения ('${taskConfig.TARGET_PATH}')") {
                File(taskConfig.TARGET_PATH).also {
                    it.mkdir()
                    Assert.assertTrue(it.exists())
                }
            }
        }
}