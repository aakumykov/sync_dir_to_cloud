package com.github.aakumykov.sync_dir_to_cloud.bb_new.common

import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestCloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO

object TestComponentHolder {

    val testSyncTaskDAO: TestSyncTaskDAO
    val testCloudAuthDAO: TestCloudAuthDAO
    val testSyncInstructionDAO: TestSyncInstructionDAO

    @Deprecated("Не используется")
    private val testSyncObjectDAO: TestSyncObjectDAO

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