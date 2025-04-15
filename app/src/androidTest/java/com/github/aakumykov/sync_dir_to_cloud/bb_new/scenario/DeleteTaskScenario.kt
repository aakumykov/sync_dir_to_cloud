package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class DeleteTaskScenario(
    override val taskConfig: TaskConfig = LocalTaskConfig
) : TaskScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание задачи с id='${taskId}'") {
            dao.delete(taskId)
            Assert.assertNull(dao.get(taskId))
        }
    }
}