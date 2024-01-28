package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    // TODO: OutputData: краткая сводка о выполненной работе

    override suspend fun doWork(): Result {
        MyLogger.d(TAG, "doWork(${hashCode()})")

        val taskId: String = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData("Task id not found in input data"))

        try {
            MyLogger.d(TAG, "executeSyncTask()")
            App.getAppComponent().getSyncTaskExecutor().executeSyncTask(taskId)
        }
        catch (t: Throwable) {
            MyLogger.e(TAG, ExceptionUtils.getErrorMessage(t), t)
            Result.failure(errorData(ExceptionUtils.getErrorMessage(t)))
        }

        return Result.success()
    }


    // FIXME: арогумент key подразумевается один
    private fun errorData(value: String): Data {
        return Data.Builder().apply {
            putString(ERROR_MSG, value)
        }.build()
    }

    companion object {
        fun dataWithTaskId(taskId: String): Data
            = Data.Builder().putString(TASK_ID, taskId).build()

        val TAG: String = SyncTaskWorker::class.java.simpleName

        const val TASK_ID: String = "TASK_ID"
        const val ERROR_MSG: String = "ERROR_MSG"
    }
}