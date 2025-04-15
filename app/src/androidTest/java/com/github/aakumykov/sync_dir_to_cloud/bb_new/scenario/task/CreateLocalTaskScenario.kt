package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert


class CreateLocalTaskScenario(
    override val taskConfig: TaskConfig = LocalTaskConfig
) : TaskScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        step("Создание авторизации") {
            // Для локального TARGET == SOURCE, поэтому добавляю только один.
            authDao.add(taskConfig.SOURCE_AUTH)
            Assert.assertNotNull(authDao.get(taskConfig.SOURCE_AUTH_ID))
        }

        step("Создание задачи с id='${taskId}'") {
            taskDao.add(taskConfig.TASK_SYNC)
            Assert.assertNotNull(taskDao.get(taskId))
        }
    }
}


