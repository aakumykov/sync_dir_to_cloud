package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    @Inject lateinit var syncTaskUpdater: SyncTaskUpdater
    @Inject lateinit var syncTaskReader: SyncTaskReader
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        App.appComponent().injectSyncTaskWorker(this)
    }

    // TODO: переделать в CoroutineWorker
    override fun doWork(): Result {

        val taskId: String = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData("Task is is null"))

        // FIXME: как прервать корутину, если syncTask == null ?
        scope.launch {

            val syncTask = syncTaskReader.getSyncTask(taskId)

            if (null != syncTask) {
                syncTask.setIsProgress(true)
                syncTaskUpdater.updateSyncTask(syncTask)

                delay(1000)

                syncTask.setIsProgress(false)
                syncTaskUpdater.updateSyncTask(syncTask)
            }
        }

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