package com.github.aakumykov.sync_dir_to_cloud.bb_new.objects

import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.TestSyncTaskDAO

object SyncTaskTestObject {

    val dao: TestSyncTaskDAO

    init {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app: App = instrumentation.targetContext.applicationContext as App
        val testComponent = app.component() as TestComponent
        dao = testComponent.getTestSyncTaskDAO()
    }
}