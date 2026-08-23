package com.github.aakumykov.sync_dir_to_cloud.bb_new.common

import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestCloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_processor.SyncTaskProcessor
import kotlinx.coroutines.CoroutineScope

object TestComponentHolder {

    val testSyncTaskDAO: TestSyncTaskDAO
    val testSyncObjectDAO: TestSyncObjectDAO
    val testCloudAuthDAO: TestCloudAuthDAO
    val testSyncInstructionDAO: TestSyncInstructionDAO

    internal val testComponent: TestComponent

    init {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app: App = instrumentation.targetContext.applicationContext as App
        testComponent = app.component() as TestComponent
        testSyncTaskDAO = testComponent.testSyncTaskDAO()
        testCloudAuthDAO = testComponent.testCloudAuthDAO()
        testSyncObjectDAO = testComponent.testSyncObjectDAO()
        testSyncInstructionDAO = testComponent.testSyncInstructionDAO()
    }
}


val testComponent: TestComponent
    get() = TestComponentHolder.testComponent


fun syncTaskProcessor(
    syncTask: SyncTask,
    executionId: String,
    coroutineScope: CoroutineScope,
    notificator: SyncTaskNotificator,
): SyncTaskProcessor {
    return testComponent
        .getSyncTaskProcessorAssistedFactory()
        .create(syncTask,executionId, coroutineScope, notificator)
}
