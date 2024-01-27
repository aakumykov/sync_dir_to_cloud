package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.*
import com.github.aakumykov.sync_dir_to_cloud.config.WorkManagerConfig
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
        workManager.enqueueUniquePeriodicWork(
            workId(syncTask),
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest(syncTask)
        ).state.observeForever {
            when(it) {
                is Operation.State.SUCCESS -> callbacks.onSyncTaskScheduleSuccess()
                is Operation.State.IN_PROGRESS -> {}
                is Operation.State.FAILURE -> callbacks.onSyncTaskScheduleError(it.throwable)
            }
        }
    }

    private fun workId(syncTask: SyncTask): String {
        return WorkManagerConfig.PERIODIC_WORK_ID_PREFIX + syncTask.id
    }

    override fun unScheduleSyncTask(
        syncTask: SyncTask,
        callbacks: SyncTaskScheduler.UnScheduleCallbacks
    ) {
        workManager
            .cancelUniqueWork(workId(syncTask))
            .state.observeForever {
            when(it) {
                is Operation.State.SUCCESS -> callbacks.onSyncTaskUnScheduleSuccess()
                is Operation.State.IN_PROGRESS -> {}
                is Operation.State.FAILURE -> callbacks.onSyncTaskUnScheduleError(it.throwable)
            }
        }
    }


    private fun periodicWorkRequest(syncTask: SyncTask): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            SyncTaskWorker::class.java,
            syncTask.getExecutionIntervalMinutes(),
            TimeUnit.MINUTES,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(SyncTask.TAG)
            .setInputData(SyncTaskWorker.dataWithTaskId(syncTask.id))
            .setConstraints(batteryConstraints())
            .build()
    }

    private fun batteryConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
    }
}

