package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.common

import com.github.aakumykov.sync_dir_to_cloud.bb_new.system.TestComponentHolder
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestCloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario

abstract class TaskScenario() : Scenario() {
    protected val taskDao: TestSyncTaskDAO get() = TestComponentHolder.testSyncTaskDAO
    protected val authDao: TestCloudAuthDAO get() = TestComponentHolder.testCloudAuthDAO

    protected abstract val taskConfig: TaskConfig

    protected val taskId: String get() = taskConfig.TASK_ID
}


