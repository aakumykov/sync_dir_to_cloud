package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext

class CreateFirstTargetFileScenario : FileScenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание одного файла в источнике") {
            fileHelper.createTargetFile1()
            // FIXME: по идее, нужно проверять здесь. Но зачем тогда тест FileHelper-а?
        }
    }
}