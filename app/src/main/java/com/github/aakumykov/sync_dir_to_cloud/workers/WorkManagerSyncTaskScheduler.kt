package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.*
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerSyncTaskScheduler @Inject constructor(
    private val workManager: WorkManager,
) : SyncTaskScheduler {

    override fun scheduleSyncTask(
        syncTask: SyncTask,
        callbacks: SyncTaskScheduler.ScheduleCallbacks
    ) {
        val batteryNotLowConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            SyncTaskWorker::class.java,
            syncTask.getExecutionIntervalMinutes(),
            TimeUnit.MINUTES,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(SyncTask.TAG)
            .setConstraints(batteryNotLowConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            syncTask.id,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        ).state.observeForever {
            when(it) {
                is Operation.State.SUCCESS -> callbacks.onSyncTaskScheduleSuccess()
                is Operation.State.IN_PROGRESS -> {}
                is Operation.State.FAILURE -> callbacks.onSyncTaskScheduleError(it.throwable)
            }
        }
    }

    override fun unScheduleSyncTask(
        syncTask: SyncTask,
        callbacks: SyncTaskScheduler.UnScheduleCallbacks
    ) {
        workManager.cancelUniqueWork(syncTask.id).state.observeForever {
            when(it) {
                is Operation.State.SUCCESS -> callbacks.onSyncTaskUnScheduleSuccess()
                is Operation.State.IN_PROGRESS -> {}
                is Operation.State.FAILURE -> callbacks.onSyncTaskUnScheduleError(it.throwable)
            }
        }
    }

    private fun workName(taskId: String) = PERIODIC_WORK_NAME_PREFIX + taskId

    companion object {
        const val PERIODIC_WORK_NAME_PREFIX = "PERIODIC-"
    }
}

