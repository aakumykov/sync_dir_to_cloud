package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import com.github.aakumykov.sync_dir_to_cloud.utils.SimpleFileWriter
import kotlinx.coroutines.delay
import javax.inject.Inject

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var syncTaskUpdater: SyncTaskUpdater

    @Inject
    lateinit var syncTaskReader: SyncTaskReader

    private val context: Context

    init {
        App.appComponent().injectWorker2(this)
        this.context = context
    }

    override suspend fun doWork(): Result {

        val fileWriter = SimpleFileWriter(context.cacheDir, SyncTaskWorker::class.simpleName+".log")

        fileWriter.writeln(CurrentDateTime.get()+", start")

        val taskId = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData("taskId is null"))

        val syncTask = syncTaskReader.getSyncTask(taskId)
            ?: return Result.failure(errorData("SyncTask is null"))

        syncTask.state = SyncTask.State.RUNNING
        syncTaskUpdater.updateSyncTask(syncTask)

        delay(3000)

        syncTask.state = SyncTask.State.SUCCESS
        syncTaskUpdater.updateSyncTask(syncTask)

        fileWriter.writeln(CurrentDateTime.get()+", end")

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