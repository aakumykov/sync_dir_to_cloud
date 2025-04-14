package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.task

import com.github.aakumykov.sync_dir_to_cloud.aa_old.common.dao_set.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class DeleteLocalTask(
    private val taskConfig: TaskConfig,
    private val daoSet: DaoSet
) : SyncTaskScenario(daoSet) {

    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            step("Удаление задачи"){

                val taskId = taskConfig.TASK_ID
                val sourceAuthId = taskConfig.AUTH_ID
                val targetAuthId = taskConfig.AUTH_ID

                runTest {
                    syncTaskDAO.get(taskId)?.also { syncTask: SyncTask ->
                        syncTaskDAO.delete(syncTask)
                        Assert.assertNull(syncTaskDAO.get(syncTask.id))

                        cloudAuthDAO.testDelete(sourceAuthId)
                        Assert.assertNull((cloudAuthDAO.get(syncTask.sourceAuthId!!)))

                        cloudAuthDAO.testDelete(targetAuthId)
                        Assert.assertNull((cloudAuthDAO.get(syncTask.targetAuthId!!)))
                    }
                }
            }
        }
}