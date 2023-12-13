package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncTaskWorker2(private val context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(TASK_ID)
        return taskId?.let { startTask(taskId) } ?: Result.failure()
    }

    private suspend fun startTask(taskId: String): Result {
        try {
            val syncTaskReader = App.getAppComponent().getSyncTaskReader()
            val syncTask = syncTaskReader.getSyncTask(taskId)
            val syncTaskExecutor = App.getAppComponent().getSyncTaskExecutor()
            syncTaskExecutor.executeSyncTask(syncTask)
            return Result.success()
        }
        catch (t: Throwable) {
            Log.e(TAG, messageOrClassName(t))
            return Result.failure(errorData(t))
        }
    }

    private fun errorData(t: Throwable): Data {
        return Data.Builder().apply {
            putString(ERROR_MSG, t.message ?: t.javaClass.name)
        }.build()
    }

    companion object {
        val TAG: String = SyncTaskWorker2::class.java.simpleName
        const val TASK_ID = "TASK_ID"
        const val ERROR_MSG = "ERROR_MSG"
    }
}