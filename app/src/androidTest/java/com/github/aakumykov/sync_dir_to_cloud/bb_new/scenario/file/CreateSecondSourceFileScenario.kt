package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext

class CreateSecondSourceFileScenario : FileScenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание второго файла в источнике") {
            fileHelper.createSourceFile2()
            // FIXME: по идее, нужно проверять здесь. Но зачем тогда тест FileHelper-а?
        }
    }
}