package com.github.aakumykov.sync_dir_to_cloud.scenario.task

import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig.AUTH_ID
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig.TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig.SOURCE_PATH
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig.STORAGE_TYPE
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig.TARGET_PATH
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.common.dao_set.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.TaskConfig
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest

class CreateLocalTask(
    private val taskConfig: TaskConfig,
    private val daoSet: DaoSet,
) : Scenario(), DaoSet by daoSet {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание CloudAuth") {
            runTest {
                val cloudAuth = CloudAuth(
                    id = taskConfig.AUTH_ID,
                    name = taskConfig.AUTH_NAME,
                    authToken = taskConfig.AUTH_TOKEN,
                    storageType = taskConfig.STORAGE_TYPE,
                )
                cloudAuthDAO.add(cloudAuth)

//                val readCloudAuth = cloudAuthDAO.get(AUTH_ID)
//
//                Assert.assertEquals(cloudAuth.id, readCloudAuth.id)
//                Assert.assertEquals(cloudAuth.name, readCloudAuth.name)
//                Assert.assertEquals(cloudAuth.authToken, readCloudAuth.authToken)
//                Assert.assertEquals(cloudAuth.storageType, readCloudAuth.storageType)
            }
        }

        step("Создание SyncTask (типа '${taskConfig.SYNC_MODE}')") {
            runTest {
                val syncTask = syncTaskWithMode(taskConfig.SYNC_MODE)
                syncTaskDAO.add(syncTask)

//                val readSyncTask = syncTaskDAO.get(TASK_ID)
//
//                Assert.assertEquals(syncTask.id, readSyncTask.id)
//                Assert.assertEquals(syncTask.syncMode, readSyncTask.syncMode)
//                Assert.assertEquals(syncTask.sourcePath, readSyncTask.sourcePath)
//                Assert.assertEquals(syncTask.targetPath, readSyncTask.targetPath)
//                Assert.assertEquals(syncTask.sourceStorageType, readSyncTask.sourceStorageType)
//                Assert.assertEquals(syncTask.targetStorageType, readSyncTask.targetStorageType)
//                Assert.assertEquals(syncTask.intervalHours, readSyncTask.intervalHours)
//                Assert.assertEquals(syncTask.intervalMinutes, readSyncTask.intervalMinutes)
//                Assert.assertEquals(syncTask.sourceAuthId, readSyncTask.sourceAuthId)
//                Assert.assertEquals(syncTask.targetAuthId, readSyncTask.targetAuthId)
            }
        }
    }

    private fun syncTaskWithMode(syncMode: SyncMode): SyncTask = SyncTask(
        sourcePath = SOURCE_PATH,
        targetPath = TARGET_PATH,
        sourceStorageType = STORAGE_TYPE,
        targetStorageType = STORAGE_TYPE,
        syncMode = syncMode,
        intervalHours = 0,
        intervalMinutes = 0,
    ).apply {
        id = TASK_ID
        this.sourceAuthId = AUTH_ID
        this.targetAuthId = AUTH_ID
    }
}