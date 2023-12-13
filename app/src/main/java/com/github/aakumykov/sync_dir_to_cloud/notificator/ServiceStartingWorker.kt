package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class ServiceStartingWorker(private val context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {

        val cmd = inputData.getString(COMMAND) ?: return errorResult("Command not found in worker input data.")
        val taskId = inputData.getString(TASK_ID) ?: return errorResult("Task id not found in worker input data.")

        return when (cmd) {
            COMMAND_START -> startWorkingService(taskId)
            else -> {
                errorResult("Неизвестная команда: $cmd")
            }
        }
    }

    private fun startWorkingService(taskId: String): Result {
        return when (context.startService(serviceStartingIntent(taskId))) {
            null -> errorResult("Cannot starting service")
            else -> successResult()
        }
    }

    private fun serviceStartingIntent(taskId: String): Intent {
        return Intent(context, SyncTaskExecutionService::class.java).apply {
            setAction(SyncTaskExecutionService.ACTION_START_WORK)
            putExtra(TASK_ID, taskId)
        }
    }


    companion object {
        val TAG: String = ServiceStartingWorker::class.java.simpleName
        const val COMMAND = "COMMAND"
        const val COMMAND_START = "START"
        const val TASK_ID = "TASK_ID"
        const val ERROR_MSG = "ERROR_MSG"
    }
}