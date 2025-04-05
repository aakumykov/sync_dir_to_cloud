package com.github.aakumykov.sync_dir_to_cloud.scenario

import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.AUTH_ID
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.AUTH_NAME
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.AUTH_TOKEN
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.SOURCE_PATH
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.STORAGE_TYPE
import com.github.aakumykov.sync_dir_to_cloud.config.TestTaskConfig.TARGET_PATH
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.common.DbStuff
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class CreateAndCheckLocalTask(
    private val syncMode: SyncMode,
    private val dbStuff: DbStuff,
) : Scenario(), DbStuff by dbStuff {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание CloudAuth") {
            runTest {
                val cloudAuth = CloudAuth(
                    id = AUTH_ID,
                    name = AUTH_NAME,
                    authToken = AUTH_TOKEN,
                    storageType = STORAGE_TYPE,
                )
                cloudAuthDAO.add(cloudAuth)

                val readCloudAuth = cloudAuthDAO.get(AUTH_ID)

                Assert.assertEquals(cloudAuth.id, readCloudAuth.id)
                Assert.assertEquals(cloudAuth.name, readCloudAuth.name)
                Assert.assertEquals(cloudAuth.authToken, readCloudAuth.authToken)
                Assert.assertEquals(cloudAuth.storageType, readCloudAuth.storageType)
            }
        }

        step("Создание SyncTask (типа SYNC)") {
            runTest {
                val syncTask = syncTaskWithMode(syncMode)
                syncTaskDAO.add(syncTask)

                val readSyncTask = syncTaskDAO.get(TASK_ID)

                Assert.assertEquals(syncTask.id, readSyncTask.id)
                Assert.assertEquals(syncTask.syncMode, readSyncTask.syncMode)
                Assert.assertEquals(syncTask.sourcePath, readSyncTask.sourcePath)
                Assert.assertEquals(syncTask.targetPath, readSyncTask.targetPath)
                Assert.assertEquals(syncTask.sourceStorageType, readSyncTask.sourceStorageType)
                Assert.assertEquals(syncTask.targetStorageType, readSyncTask.targetStorageType)
                Assert.assertEquals(syncTask.intervalHours, readSyncTask.intervalHours)
                Assert.assertEquals(syncTask.intervalMinutes, readSyncTask.intervalMinutes)
                Assert.assertEquals(syncTask.sourceAuthId, readSyncTask.sourceAuthId)
                Assert.assertEquals(syncTask.targetAuthId, readSyncTask.targetAuthId)
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