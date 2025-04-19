package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.testComponent
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

class RunSyncScenario(
    private val taskConfig: TaskConfig = LocalTaskConfig()
) : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        runBlocking {
            syncTaskExecutor(this).executeSyncTask(taskConfig.TASK_ID)
        }
    }

    private fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return testComponent
            .getSyncTaskExecutorAssistedFactory()
            .create(scope)
    }
}