package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert
import java.io.File


abstract class DeleteAllFilesInDirScenario : FileScenario() {

    abstract val dir: File

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Удаление содержимого каталога '${dir.absolutePath}'") {
            fileHelper.deleteAllFilesInDir(dir)
            Assert.assertEquals(0, dir.list()?.size ?: -1)
        }
    }
}
