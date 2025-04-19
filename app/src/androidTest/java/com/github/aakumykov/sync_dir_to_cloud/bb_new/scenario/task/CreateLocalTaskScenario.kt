package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.common.LocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.common.TaskScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert


class CreateLocalTaskScenario(
    override val taskConfig: TaskConfig = LocalTaskConfig()
) : LocalTaskScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        step("Создание авторизации") {
            authDao.add(taskConfig.SOURCE_AUTH)
            authDao.get(authId).also {
                Assert.assertNotNull(it)
            }
        }

        // FIXME: проверять, что у задачи установлены AuthId

        step("Создание задачи с id='${taskId}'") {
            taskDao.add(taskConfig.TASK_SYNC)

            taskDao.get(taskId).also {
                Assert.assertNotNull(it)
                Assert.assertNotNull(it!!.sourceAuthId)
                Assert.assertNotNull(it.targetAuthId)

            } ?: throw NoSuchElementException("Нет задачи задачи с id='${taskId}'")
        }
    }
}


