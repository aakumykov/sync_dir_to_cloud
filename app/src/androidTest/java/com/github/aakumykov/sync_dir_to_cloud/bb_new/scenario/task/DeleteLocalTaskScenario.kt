package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.common.LocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.common.TaskScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class DeleteLocalTaskScenario(
    override val taskConfig: TaskConfig = LocalTaskConfig
) : LocalTaskScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        step("Удаление авторизации с id='${taskConfig.SOURCE_AUTH_ID}'") {
            authDao.delete(authId)
            Assert.assertNull(authDao.get(authId))
        }

        step("Удаление задачи с id='${taskId}'") {
            taskDao.delete(taskId)
            Assert.assertNull(taskDao.get(taskId))
        }
    }
}