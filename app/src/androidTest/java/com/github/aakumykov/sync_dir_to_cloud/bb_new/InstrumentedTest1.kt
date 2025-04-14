package com.github.aakumykov.sync_dir_to_cloud.bb_new

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class InstrumentedTest1 {

    @Inject
    lateinit var testSyncTaskDAO: TestSyncTaskDAO

    @Before
    fun prepare() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        val app: App = instrumentation.targetContext.applicationContext as App

        val testComponent = app.component() as TestComponent

        testComponent.injectInstrumentedTest1(this)
    }

    @Test
    fun test1() {

    }
}