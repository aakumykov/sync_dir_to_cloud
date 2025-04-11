package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario

import com.github.aakumykov.sync_dir_to_cloud.aa_old.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.aa_old.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest

class RunSync(private val taskConfig: TaskConfig) : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("") {
            runTest {
                syncTaskExecutor(this).executeSyncTask(taskConfig.TASK_ID)
            }
        }
    }

    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return appComponent.getSyncTaskExecutorAssistedFactory().create(scope)
    }

    private fun runSync() = runTest {
        syncTaskExecutor(this).executeSyncTask(LocalTaskConfig.TASK_ID)
    }
}