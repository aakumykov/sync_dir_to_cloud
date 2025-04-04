package com.github.aakumykov.sync_dir_to_cloud

import android.os.Build
import android.os.Environment
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.scenario.CreateLocalTask
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Test
import java.io.File

class TaskCreationTest2() : TestCase() {

    private val appDatabase by lazy { Room.inMemoryDatabaseBuilder(device.targetContext, AppDatabase::class.java).build() }
    private val cloudAuthDAO: CloudAuthDAO by lazy { appDatabase.getCloudAuthDAO() }
    private val syncTaskDAO: SyncTaskDAO by lazy { appDatabase.getSyncTaskDAO() }
    private val testTaskCreator by lazy { appComponent.getTestTaskCreator() }

    @Test
    fun when_create_test_task_then_correct_entities_are_created() = run {
        scenario(
            CreateLocalTask(SyncMode.SYNC, device.targetContext)
        )
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