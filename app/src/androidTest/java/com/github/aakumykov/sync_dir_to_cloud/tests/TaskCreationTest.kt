package com.github.aakumykov.sync_dir_to_cloud.tests

import androidx.test.ext.junit.rules.activityScenarioRule
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateTaskScenario
import com.github.aakumykov.sync_dir_to_cloud.scenario.RemoveAllTasksScenario
import com.github.aakumykov.sync_dir_to_cloud.view.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test

class TaskCreationTest : TestCase() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun newTaskCanBeCreated() = run {
        scenario(
            RemoveAllTasksScenario()
        )
        /*scenario(
            CreateTaskScenario()
        )*/
    }
}