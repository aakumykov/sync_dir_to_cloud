package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.CreateTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.DeleteTaskScenario
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Test

class TaskCreationTest : TestCase() {

    @Test
    fun createTask() = run {
        scenario(CreateTaskScenario())
    }

    @Test
    fun deleteTask() = run {
        scenario(DeleteTaskScenario())
    }
}