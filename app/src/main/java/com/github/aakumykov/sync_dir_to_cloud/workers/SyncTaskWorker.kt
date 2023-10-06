package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import java.util.concurrent.TimeUnit

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    private val syncTaskUpdater: SyncTaskUpdater = App.appComponent().getSyncTaskUpdater()

    override fun doWork(): Result {

        TimeUnit.SECONDS.sleep(1)

        return Result.success()
    }
}