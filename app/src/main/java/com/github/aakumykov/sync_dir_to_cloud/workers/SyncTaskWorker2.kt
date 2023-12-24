package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App

class SyncTaskWorker2(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val taskId: String = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData(ERROR_MSG, "Task id not found in input data"))

        val syncTask = App.getAppComponent().getSyncTaskReader().getSyncTask(taskId)

//        App.getAppComponent().getSyncTaskExecutor().executeSyncTask(syncTask)
        App.getAppComponent().getSyncTaskExecutor2().executeSyncTask(syncTask)

        return Result.success()
    }

    private fun errorData(key: String, value: String): Data {
        return Data.Builder().apply {
            putString(key, value)
        }.build()
    }

    companion object {
        const val TASK_ID: String = "TASK_ID"
        const val ERROR_MSG: String = "ERROR_MSG"
    }
}