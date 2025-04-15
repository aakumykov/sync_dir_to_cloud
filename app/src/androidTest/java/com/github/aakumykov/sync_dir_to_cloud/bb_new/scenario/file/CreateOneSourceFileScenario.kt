package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common.FileScenario
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Before

abstract class FileScenario : Scenario() {

}

class CreateOneSourceFileScenario : FileScenario() {

    @Before
    fun deleteAllSourceFiles() {
        Log.d("","")
    }

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание файла в источнике") {

        }
    }
}