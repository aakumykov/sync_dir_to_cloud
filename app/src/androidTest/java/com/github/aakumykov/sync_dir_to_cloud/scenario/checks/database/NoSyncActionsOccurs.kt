package com.github.aakumykov.sync_dir_to_cloud.scenario.checks.file_checks.db_checks

import com.github.aakumykov.sync_dir_to_cloud.common.dao_set.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.scenario.RunSync
import com.github.aakumykov.sync_dir_to_cloud.scenario.common.FileScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class NoSyncActionsOccurs(private val daoSet: DaoSet) : FileScenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Действий по синхронизации не производилось") {
            runTest {
                val logItemsCountBeforeSync = syncObjectLogDAO.testListAllLogItems()
                scenario(RunSync(LocalTaskConfig))
                val logItemsCountAfterSync = syncObjectLogDAO.testListAllLogItems()
                Assert.assertEquals(logItemsCountBeforeSync, logItemsCountAfterSync)
            }
        }
    }

    private val syncObjectLogDAO get() = daoSet.syncObjectLogDAO
}
