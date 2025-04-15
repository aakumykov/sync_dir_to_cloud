package com.github.aakumykov.sync_dir_to_cloud.bb_new.common

import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.SyncTaskTestObject
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.TestSyncTaskDAO
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Assert

abstract class SyncTaskTestCase() : TestCase() {

//    @Inject
    protected var dao: TestSyncTaskDAO = SyncTaskTestObject.dao

    protected val taskConfig = LocalTaskConfig

    protected val taskId: String get() = taskConfig.TASK_ID


    /*@Before
    fun prepare() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app: App = instrumentation.targetContext.applicationContext as App
        val testComponent = app.component() as TestComponent
        dao = testComponent.getTestSyncTaskDAO()
    }*/


    protected fun createTask() = run {
        step("Создание задачи '${taskId}'") {
            dao.add(taskConfig.TASK_SYNC)
        }
    }


    protected fun deleteTask() = run {
        step("Удаление задачи '${taskId}'") {
            dao.delete(taskId)
            Assert.assertNull(dao.get(taskId))
        }
    }


    protected fun checkTaskExists() {
        Assert.assertNotNull(dao.get(taskId))
    }


    protected fun checkTaskNotExists() {
        Assert.assertNull(dao.get(taskId))
    }
}