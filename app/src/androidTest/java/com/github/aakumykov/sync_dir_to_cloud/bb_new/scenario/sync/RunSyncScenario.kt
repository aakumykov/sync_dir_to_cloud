package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.syncTaskProcessor
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.testComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_processor.SyncTaskProcessor
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest

class RunSyncScenario(
    private val syncTask: SyncTask,
    private val executionId: String,
    private val notificator: SyncTaskNotificator,
) : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        runTest {
            syncTaskProcessor(
                syncTask = syncTask,
                executionId = executionId,
                coroutineScope = this,
                notificator = notificator
            ).processSyncTask()
        }
    }
}