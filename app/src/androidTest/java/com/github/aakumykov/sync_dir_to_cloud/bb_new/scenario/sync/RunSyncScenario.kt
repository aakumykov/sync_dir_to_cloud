package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest

class RunSyncScenario(
    private val taskConfig: TaskConfig = LocalTaskConfig
) : SyncScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        runBlocking {
            syncTaskExecutor(this).executeSyncTask(taskConfig.TASK_ID)
        }
    }
}