package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync

import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.testComponent
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import kotlinx.coroutines.CoroutineScope

abstract class SyncScenario : Scenario() {

    protected fun syncTaskExecutor(scope: CoroutineScope): SyncTaskExecutor {
        return testComponent
            .getSyncTaskExecutorAssistedFactory()
            .create(scope)
    }
}