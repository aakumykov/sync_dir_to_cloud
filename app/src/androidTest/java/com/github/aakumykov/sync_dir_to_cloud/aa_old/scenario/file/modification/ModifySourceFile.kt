package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.file.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.common.FileScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.randomBytes
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class ModifySourceFile(private val localFileCofnig: LocalFileCofnig) : FileScenario() {
    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            val newFileContents = randomBytes(15)
            testFileManager.modifyFileInSource(
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