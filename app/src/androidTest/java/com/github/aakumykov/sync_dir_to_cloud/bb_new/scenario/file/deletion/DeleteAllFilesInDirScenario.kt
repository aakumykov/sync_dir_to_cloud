package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert
import java.io.File


abstract class DeleteAllFilesInDirScenario : FileScenario() {

    abstract val dir: File
    
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Удаление содержимого каталога '${dir.absolutePath}'") {
            deleteAllFilesInDirWithCheck(dir)
            Assert.assertEquals(0, dir.list()?.size ?: 0)
        }
    }

    private fun deleteAllFilesInDirWithCheck(dir: File) {

        if (!dir.isDirectory)
            throw IllegalArgumentException("Argument is not a directory: '${dir.absolutePath}'")

        dir.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }
}
