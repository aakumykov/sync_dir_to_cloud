package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileAndDbScenario
import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.FileScenario
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext

class CreateSourceFile(
    fileName: String,
    fileSize: Int = LocalFileCofnig.DEFAULT_FILE_SIZE
)
    : FileScenario()
{
    override val steps: TestContext<Unit>.() -> Unit = {
        fileHelper.createFileInSource(fileName, fileSize)
    }
}


class CreateFileScenario(
    private val syncSide: SyncSide,
    private val fileName: String,
    private val stateInStorage: StateInStorage,
    private val fileSize: Int = LocalFileCofnig.DEFAULT_FILE_SIZE
)
    : FileAndDbScenario()
{
    override val steps: TestContext<Unit>.() -> Unit = {

        when(syncSide) {
            SyncSide.SOURCE -> fileHelper.createFileInSource(fileName, fileSize)
            SyncSide.TARGET -> fileHelper.createFileInTarget(fileName, fileSize)
        }

        /*when(syncSide) {
            SyncSide.SOURCE -> testSyncObjectDAO.add()
        }*/
    }
}