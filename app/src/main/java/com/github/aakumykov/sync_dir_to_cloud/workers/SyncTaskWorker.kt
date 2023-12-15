package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import com.github.aakumykov.sync_dir_to_cloud.utils.SimpleFileWriter
import kotlinx.coroutines.delay
import javax.inject.Inject

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    @Inject lateinit var syncTaskUpdater: SyncTaskUpdater
    @Inject lateinit var syncTaskReader: SyncTaskReader
    private val context: Context
    private lateinit var currentTask: SyncTask

    init {
        App.getAppComponent().injectWorker2(this)
        this.context = context
    }


    override suspend fun doWork(): Result {

        val fileWriter = SimpleFileWriter(context.cacheDir, SyncTaskWorker::class.simpleName+".log")
        fileWriter.writeln(CurrentDateTime.get()+", start")

        val taskId = inputData.getString(TASK_ID)
            ?: return Result.failure(data(ERROR_MSG,"taskId is null"))

        currentTask = syncTaskReader.getSyncTask(taskId)

        if (currentTask.state == SyncTask.State.WRITING_TARGET)
            return Result.retry()

        changeTaskState(SyncTask.State.WRITING_TARGET)
        delay(10000)
        changeTaskState(SyncTask.State.SUCCESS)

        fileWriter.writeln(CurrentDateTime.get()+", end")

        return Result.success()
    }


    private fun changeTaskState(state: SyncTask.State) {
        currentTask.state = state
        syncTaskUpdater.updateSyncTask(currentTask)
    }


    private fun data(key: String, message: String): Data {
        return Data.Builder().apply {
            putString(key, message)
        }.build()
    }


    companion object {
        const val TASK_ID = "TASK_ID"
        const val SUCCESS_MSG = "SUCCESS_MSG"
        const val ERROR_MSG = "ERROR_MSG"
    }
}