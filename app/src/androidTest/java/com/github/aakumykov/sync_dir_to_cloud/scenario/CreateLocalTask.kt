package com.github.aakumykov.sync_dir_to_cloud.scenario

import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_AUTH_ID
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_AUTH_NAME
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_AUTH_TOKEN
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_STORAGE_TYPE
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_TASK_SOURCE_PATH
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_TASK_STORAGE_TYPE
import com.github.aakumykov.sync_dir_to_cloud.TaskCreationTest2.Companion.TEST_TASK_TARGET_PATH
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class CreateLocalTask(
    private val syncMode: SyncMode,
    private val targetContext: Context,
) : Scenario() {

    private val appDatabase by lazy { Room.inMemoryDatabaseBuilder(targetContext, AppDatabase::class.java).build() }
    private val cloudAuthDAO: CloudAuthDAO by lazy { appDatabase.getCloudAuthDAO() }
    private val syncTaskDAO: SyncTaskDAO by lazy { appDatabase.getSyncTaskDAO() }
    private val testTaskCreator by lazy { appComponent.getTestTaskCreator() }


    override val steps: TestContext<Unit>.() -> Unit = {
        step("Создание CloudAuth") {
            runTest {
                val cloudAuth = CloudAuth(
                    id = TEST_AUTH_ID,
                    name = TEST_AUTH_NAME,
                    authToken = TEST_AUTH_TOKEN,
                    storageType = TEST_STORAGE_TYPE,
                )
                cloudAuthDAO.add(cloudAuth)

                val readedCloudAuth = cloudAuthDAO.get(TEST_AUTH_ID)

                Assert.assertEquals(cloudAuth.id, readedCloudAuth.id)
                Assert.assertEquals(cloudAuth.name, readedCloudAuth.name)
                Assert.assertEquals(cloudAuth.authToken, readedCloudAuth.authToken)
                Assert.assertEquals(cloudAuth.storageType, readedCloudAuth.storageType)
            }
        }
        step("Создание SyncTask (типа SYNC)") {
            runTest {
                val syncTask = syncTaskWithMode(syncMode)
                syncTaskDAO.add(syncTask)

                val readedSyncTask = syncTaskDAO.get(TEST_TASK_ID)

                Assert.assertEquals(syncTask.id, readedSyncTask.id)
                Assert.assertEquals(syncTask.syncMode, readedSyncTask.syncMode)
                Assert.assertEquals(syncTask.sourcePath, readedSyncTask.sourcePath)
                Assert.assertEquals(syncTask.targetPath, readedSyncTask.targetPath)
                Assert.assertEquals(syncTask.sourceStorageType, readedSyncTask.sourceStorageType)
                Assert.assertEquals(syncTask.targetStorageType, readedSyncTask.targetStorageType)
                Assert.assertEquals(syncTask.intervalHours, readedSyncTask.intervalHours)
                Assert.assertEquals(syncTask.intervalMinutes, readedSyncTask.intervalMinutes)
                Assert.assertEquals(syncTask.sourceAuthId, readedSyncTask.sourceAuthId)
                Assert.assertEquals(syncTask.targetAuthId, readedSyncTask.targetAuthId)
            }
        }
    }

    private fun syncTaskWithMode(syncMode: SyncMode): SyncTask = SyncTask(
        sourcePath = TEST_TASK_SOURCE_PATH,
        targetPath = TEST_TASK_TARGET_PATH,
        sourceStorageType = TEST_TASK_STORAGE_TYPE,
        targetStorageType = TEST_TASK_STORAGE_TYPE,
        syncMode = syncMode,
        intervalHours = 0,
        intervalMinutes = 0,
    ).apply {
        id = TEST_TASK_ID
        this.sourceAuthId = TEST_AUTH_ID
        this.targetAuthId = TEST_AUTH_ID
    }
}