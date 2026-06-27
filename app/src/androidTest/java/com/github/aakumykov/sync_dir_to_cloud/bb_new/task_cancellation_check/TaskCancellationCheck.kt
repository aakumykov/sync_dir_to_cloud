package com.github.aakumykov.sync_dir_to_cloud.bb_new.task_cancellation_check

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.testComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.readOnlyTargetNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.kaspersky.kaspresso.internal.extensions.other.createDirIfNeeded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TaskCancellationCheck {

    private val targetContext get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val cacheDir: File get() = targetContext.cacheDir

    private val sourceDirName by lazy { newRandomId }
    private val targetDirName by lazy { newRandomId }

    private val sourcePath: String by lazy {
        File(cacheDir, sourceDirName).createDirIfNeeded().absolutePath
    }

    private val targetPath: String by lazy {
        File(cacheDir, targetDirName).createDirIfNeeded().absolutePath
    }

    private val syncTask: SyncTask get() = SyncTask(
        sourcePath = sourcePath,
        targetPath = targetPath,
        sourceStorageType = StorageType.LOCAL,
        targetStorageType = StorageType.LOCAL,
        syncMode = SyncMode.SYNC,
        intervalHours = 1,
        intervalMinutes = 1
    )

    @Test
    fun runAndCancel() = runBlocking {

        val sourceData = randomBytes
        File(sourcePath, newRandomId).writeBytes(sourceData)

        testComponent
            .getSyncTaskProcessorAssistedFactory()
            .create(
                syncTask = syncTask,
                executionId = newRandomId,
                CoroutineScope(Dispatchers.IO)
            ).processSyncTask()
    }
}