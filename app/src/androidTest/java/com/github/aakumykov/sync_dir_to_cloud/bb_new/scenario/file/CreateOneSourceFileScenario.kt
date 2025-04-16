package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.objects.LocalFileManagerHolder
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert
import org.junit.Before


abstract class FileScenario : Scenario() {
    val fileManager = LocalFileManagerHolder.fileManager
    val taskConfig = LocalTaskConfig
    val fileConfig = LocalFileCofnig
}


class CreateOneSourceFileScenario : FileScenario() {

    @Before
    fun recreateSourceDir() {
        fileManager.deleteSourceDir()
        fileManager.createSourceDir()
    }

    @Before
    fun recreateTargetDir() {
        fileManager.deleteTargetDir()
        fileManager.createTargetDir()
    }


    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание файла в источнике") {
            fileManager.createFileInSource(
                name = fileConfig.FILE_1_NAME,
                sizeKb = fileConfig.FILE_1_SIZE
            )
            Assert.assertTrue(
                fileManager
                    .fileInSource(fileConfig.FILE_1_NAME)
                    .exists()
            )
        }
    }
}