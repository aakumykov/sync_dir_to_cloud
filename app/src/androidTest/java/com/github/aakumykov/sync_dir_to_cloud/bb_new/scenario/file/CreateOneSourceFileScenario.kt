package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileHelperHolder
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext


abstract class FileScenario : Scenario() {
    val fileHelper = LocalFileHelperHolder.fileHelper
    val taskConfig = LocalTaskConfig
    val fileConfig = LocalFileCofnig
}


class CreateOneSourceFileScenario : FileScenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание одного файла в источнике") {
            fileHelper.createSourceFile1()
            // FIXME: по идее, нужно проверять здесь. Но зачем тогда тест FileHelper-а?
        }
    }
}