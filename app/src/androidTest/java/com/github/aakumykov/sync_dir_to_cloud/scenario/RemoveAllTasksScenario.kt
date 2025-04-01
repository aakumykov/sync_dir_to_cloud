package com.github.aakumykov.sync_dir_to_cloud.scenario

import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListItem
import com.github.aakumykov.sync_dir_to_cloud.screens.TaskListScreen
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import java.util.concurrent.TimeUnit

class RemoveAllTasksScenario : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {

        TaskListScreen {

            taskList.children<TaskListItem> {
                sourcePath.isVisible()
                sourcePath.hasAnyText()

                targetPath.isVisible()
                targetPath.hasAnyText()

                clickMoreButton()
                TimeUnit.SECONDS.sleep(2)
            }
        }
    }
}