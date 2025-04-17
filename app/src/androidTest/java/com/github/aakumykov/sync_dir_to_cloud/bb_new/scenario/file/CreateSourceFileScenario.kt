package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.FileConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileAndDbScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileScenario
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import java.io.File

class CreateSourceFileScenario(
    fileName: String,
    stateInStorage: StateInStorage,
    fileSize: Int = LocalFileCofnig.DEFAULT_FILE_SIZE
)
    : FileAndDbScenario()
{

    override val steps: TestContext<Unit>.() -> Unit = {
        fileHelper.createFileInSource(fileName, fileSize)
        testSyncObjectDAO.updateStateInStorageForFileName(fileName, stateInStorage)
    }
}