package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task

import com.github.aakumykov.sync_dir_to_cloud.bb_new.system.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.TestSyncTaskDAO
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class TaskScenario() : Scenario() {
    protected abstract val taskConfig: TaskConfig
    protected val taskId: String get() = taskConfig.TASK_ID
    protected val dao: TestSyncTaskDAO get() = TestComponentHolder.testSyncTaskDAO
}