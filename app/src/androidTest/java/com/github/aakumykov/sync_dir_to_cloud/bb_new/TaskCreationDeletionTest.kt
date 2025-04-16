package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.CreateLocalTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteAllTasksScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.task.DeleteLocalTaskScenario
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Test

class TaskCreationDeletionTest : TestCase() {

    @Before
    fun deleteAllTasksAndAuths() = run {
        scenario(DeleteLocalTaskScenario())
    }

    @Test
    fun createTask() = run {
        scenario(CreateLocalTaskScenario())
    }

    @Test
    fun deleteTask() = run {
        scenario(CreateLocalTaskScenario())
        scenario(DeleteLocalTaskScenario())
    }

    @Test
    fun createAndDeleteAll() = run {
        scenario(CreateLocalTaskScenario())
        scenario(DeleteAllTasksScenario())
    }
}