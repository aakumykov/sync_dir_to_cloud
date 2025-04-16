package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.common.TaskScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class DeleteAllTasksScenario(
    override val taskConfig: TaskConfig = LocalTaskConfig
) : TaskScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        step("Удаление всех авторизаций") {
            authDao.deleteAll()
            Assert.assertEquals(0, authDao.count())
        }

        step("Удаление всех задач") {
            taskDao.deleteAll()
            Assert.assertEquals(0, taskDao.count())
        }
    }
}