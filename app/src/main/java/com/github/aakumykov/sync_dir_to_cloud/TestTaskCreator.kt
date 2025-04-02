package com.github.aakumykov.sync_dir_to_cloud

import android.content.Context
import android.os.Build
import android.os.Environment
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.repository.CloudAuthRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import java.io.File
import javax.inject.Inject

class TestTaskCreator @Inject constructor(
    @AppContext private val context: Context,
    private val syncTaskRepository: SyncTaskRepository,
    private val cloudAuthRepository: CloudAuthRepository,
){
    suspend fun createTestTask() {
        val cloudAuth = if (cloudAuthRepository.exists(TEST_ID))
            cloudAuthRepository.getCloudAuth(TEST_ID) else createTestCloudAuth()

        if (!syncTaskRepository.exists(TEST_ID))
            createTestSyncTask(cloudAuth.authToken,cloudAuth.authToken)
    }


    private suspend fun createTestCloudAuth(): CloudAuth {
        return CloudAuth(
            id = TEST_ID,
            name = TEST_LOCAL_CLOUD_AUTH_NAME,
            storageType = StorageType.LOCAL,
            authToken = ""
        ).apply {
            cloudAuthRepository.addCloudAuth(this)
        }
    }


    private suspend fun createTestSyncTask(sourceAuthId: String, targetAuthId: String) {
        SyncTask(
            sourcePath = testTaskLocalSourcePath,
            targetPath = testTaskLocalTargetPath,
            sourceStorageType = StorageType.LOCAL,
            targetStorageType = StorageType.LOCAL,
            syncMode = SyncMode.SYNC,
            intervalHours = 0,
            intervalMinutes = 0,
        ).apply {
            id = TEST_ID
            this.sourceAuthId = sourceAuthId
            this.targetAuthId = targetAuthId
        }.also {
            syncTaskRepository.createSyncTask(it)
        }
    }


    companion object {
        const val TEST_ID = "test-01"
        const val TEST_LOCAL_CLOUD_AUTH_NAME = "test_local_cloud_auth"

        val targetDirName: String
            get() = "d${Build.VERSION.SDK_INT}"

        val testTaskLocalTargetPath: String
            get() = File(Environment.getExternalStorageDirectory(), targetDirName).absolutePath

        val testTaskLocalSourcePath
            get() =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    }
}
