package com.github.aakumykov.sync_dir_to_cloud.scenario.file.modification

import com.github.aakumykov.sync_dir_to_cloud.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.scenario.file.FileManipulationScenario
import com.github.aakumykov.sync_dir_to_cloud.utils.randomBytes
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class ModifySourceFile(private val localFileCofnig: LocalFileCofnig) : FileManipulationScenario() {
    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            val newFileContents = randomBytes(15)
            testFilesManager.modifyFileInSource(
                fileName = localFileCofnig.FILE_1_NAME,
                fileContents = newFileContents
            ).also {
                Assert.assertTrue(it.exists())
                Assert.assertEquals(
                    newFileContents.joinToString(""),
                    it.readBytes().joinToString("")
                )
            }
        }
}