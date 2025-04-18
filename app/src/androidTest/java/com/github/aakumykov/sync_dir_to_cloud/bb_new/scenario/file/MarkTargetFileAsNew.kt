package com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.file.common.DbScenario
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Assert

class MarkTargetFileAsNew(fileName: String) : DbScenario() {
    override val steps: TestContext<Unit>.() -> Unit = {

        val syncSide = SyncSide.TARGET
        val newStateInStorage = StateInStorage.NEW

        testSyncObjectDAO.updateStateInStorageForFileName(
            fileName = fileName,
            syncSide = syncSide,
            stateInStorage = newStateInStorage
        )

        Assert.assertEquals(
            newStateInStorage,
            testSyncObjectDAO.getStateInStorage(fileName, syncSide)
        )
    }
}