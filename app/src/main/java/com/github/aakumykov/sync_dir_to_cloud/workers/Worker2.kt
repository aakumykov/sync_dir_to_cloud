package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import kotlinx.coroutines.delay
import javax.inject.Inject

class Worker2(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var syncTaskUpdater: SyncTaskUpdater

    @Inject
    lateinit var syncTaskReader: SyncTaskReader


    init {
        App.appComponent().injectWorker2(this)
    }

    override suspend fun doWork(): Result {

        val taskId = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData("taskId is null"))

        val syncTask = syncTaskReader.getSyncTask(taskId)
            ?: return Result.failure(errorData("SyncTask is null"))

        syncTask.setIsProgress(true)
        syncTaskUpdater.updateSyncTask(syncTask)

        delay(1000)

        syncTask.setIsProgress(false)
        syncTaskUpdater.updateSyncTask(syncTask)

        return Result.success()
    }

    private fun errorData(errorMsg: String): Data {
        return Data.Builder().apply {
            putString(ERROR_MSG, errorMsg)
        }.build()
    }

    companion object {
        const val TASK_ID = "TASK_ID"
        const val ERROR_MSG = "ERROR_MSG"
    }
}