package com.github.aakumykov.sync_dir_to_cloud

import android.os.Build
import android.os.Environment
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.test_utils.TestTaskCreator.Companion.testTaskLocalSourcePath
import com.github.aakumykov.sync_dir_to_cloud.test_utils.TestTaskCreator.Companion.testTaskLocalTargetPath
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File

class TaskCreationTest2() : TestCase() {

    private val appDatabase by lazy { Room.inMemoryDatabaseBuilder(device.targetContext, AppDatabase::class.java).build() }
    private val cloudAuthDAO: CloudAuthDAO by lazy { appDatabase.getCloudAuthDAO() }
    private val syncTaskDAO: SyncTaskDAO by lazy { appDatabase.getSyncTaskDAO() }
    private val testTaskCreator by lazy { appComponent.getTestTaskCreator() }

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {
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
                val syncTask = syncTaskWithMode(SyncMode.SYNC)
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


    companion object {
        const val TEST_AUTH_ID = "authId1"
        const val TEST_AUTH_NAME = "test_auth_local"
        const val TEST_AUTH_TOKEN = "test_auth_token"
        val TEST_STORAGE_TYPE = StorageType.LOCAL

        const val TEST_TASK_ID = "taskId1"
        val TEST_TASK_STORAGE_TYPE = StorageType.LOCAL
        val TEST_TASK_SOURCE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val TEST_TASK_TARGET_PATH: String = File(Environment.getExternalStorageDirectory(), "d${Build.VERSION.SDK_INT}").absolutePath
    }
}