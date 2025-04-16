package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert
import org.junit.Before


abstract class FileScenario : Scenario() {
    val fileManager = LocalFileHelperHolder.fileHelper
    val taskConfig = LocalTaskConfig
    val fileConfig = LocalFileCofnig
}


class CreateOneSourceFileScenario : FileScenario() {
    override val steps: TestContext<Unit>.() -> Unit = {

    }
}