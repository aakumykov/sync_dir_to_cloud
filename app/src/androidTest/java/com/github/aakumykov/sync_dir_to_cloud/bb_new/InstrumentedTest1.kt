package com.github.aakumykov.sync_dir_to_cloud.bb_new

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.TASK_SYNC
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class InstrumentedTest1 {

    @Inject
    lateinit var dao: TestSyncTaskDAO

    @Before
    fun prepare() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        val app: App = instrumentation.targetContext.applicationContext as App

        val testComponent = app.component() as TestComponent

        testComponent.injectInstrumentedTest1(this)
    }

    @Test
    fun when_deleteing_all_taks_then_its_count_is_zero() {
        dao.apply {
            deleteAll()
            Assert.assertEquals(
                0,
                dao.count()
            )
        }
    }

    @Test
    fun when_add_sync_task_then_count_equals_one() {
        dao.apply {
            deleteAll()
            add(TASK_SYNC)
            Assert.assertEquals(1, dao.count())
        }
    }
}