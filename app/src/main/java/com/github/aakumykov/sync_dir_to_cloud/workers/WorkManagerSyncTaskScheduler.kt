package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.config.WorkManagerConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WorkManagerSyncTaskScheduler @Inject constructor(
    private val workManager: WorkManager,
) : SyncTaskScheduler {

    // FIXME: сделать метод workName() единым для всего проекта

    override suspend fun scheduleSyncTask(syncTask: SyncTask) {
        return suspendCoroutine { continuation ->
            workManager.enqueueUniquePeriodicWork(
                workName(syncTask),
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicWorkRequest(syncTask)
            )
                .state
                .observeForever {
                when(it) {
                    is Operation.State.SUCCESS -> continuation.resume(Unit)
                    is Operation.State.IN_PROGRESS -> {}
                    is Operation.State.FAILURE -> continuation.resumeWithException(it.throwable)
                }
            }
        }
    }

    override suspend fun unScheduleSyncTask(syncTask: SyncTask) {
        return suspendCoroutine { continuation ->
            workManager
                .cancelUniqueWork(workName(syncTask))
                .state.observeForever {
                    when(it) {
                        is Operation.State.SUCCESS -> continuation.resume(Unit)
                        is Operation.State.IN_PROGRESS -> {}
                        is Operation.State.FAILURE -> continuation.resumeWithException(it.throwable)
                    }
                }
        }
    }


    private fun workName(syncTask: SyncTask): String {
        return WorkManagerConfig.PERIODIC_WORK_ID_PREFIX + syncTask.id
    }


    private fun periodicWorkRequest(syncTask: SyncTask): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            SyncTaskWorker::class.java,
            syncTask.getExecutionInterval().first,
            syncTask.getExecutionInterval().second,
            WorkManagerConfig.PERIODIC_FLEX_INTERVAL,
            WorkManagerConfig.PERIODIC_FLEX_UNITS
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

