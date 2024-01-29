package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.*
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

// TODO: передавать создатель задачи через конструктор
class WorkManagerSyncTaskStarterStopper @Inject constructor(
    private val workManager: WorkManager
) : SyncTaskStarterStopper
{
    override suspend fun startSyncTask(syncTask: SyncTask) {
        runWorkerWithData(syncTask.id, SyncTaskWorker.startCommandData(syncTask.id))
    }

    override suspend fun stopSyncTask(syncTask: SyncTask) {
        runWorkerWithData(syncTask.id, SyncTaskWorker.stopCommandData(syncTask.id))
    }

    // TODO: разобраться с возвращаемым значением
    private fun runWorkerWithData(taskId: String, inputData: Data) {
        workManager
            .beginUniqueWork(
                workName(taskId),
                ExistingWorkPolicy.KEEP,
                oneTimeWorkRequest(inputData)
            )
            .enqueue()
    }

    private fun oneTimeWorkRequest(inputData: Data): OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(SyncTaskWorker::class.java)
                .setInputData(inputData)
                .setConstraints(networkConstraints())
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
    }

    private fun networkConstraints(): Constraints {
        return Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }.build()
    }

    /*// Не нужны при ручном запуске задачи.
    private fun batteryConstraints(): Constraints {
        return Constraints.Builder().apply {
            setRequiresBatteryNotLow(true)
        }.build()
    }*/

    private fun workName(taskId: String): String = MANUAL_WORK_ID_PREFIX + taskId

    companion object {
        const val MANUAL_WORK_ID_PREFIX = "MANUAL-"
    }
}