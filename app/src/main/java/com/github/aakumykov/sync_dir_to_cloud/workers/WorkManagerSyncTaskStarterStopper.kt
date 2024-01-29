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
    override suspend fun startSyncTask(syncTask: SyncTask): Operation.State.SUCCESS {

        val workName = workName(syncTask.id)

        val inputData = Data.Builder().apply {
            putString(TASK_ID, syncTask.id)
        }.build()

        val networkConstraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }.build()

        /*val batteryConstraints = Constraints.Builder().apply {
            setRequiresBatteryNotLow(true)
        }.build()*/

        val manualSyncStartWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequest.Builder(SyncTaskWorker::class.java)
                .setInputData(inputData)
                .setConstraints(networkConstraints)
//            .setConstraints(batteryConstraints) // FIXME: при ручном запуске это ограничение неуместно
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        return workManager
            .beginUniqueWork(
                workName,
                ExistingWorkPolicy.KEEP,
                manualSyncStartWorkRequest
            )
            .enqueue()
            .await()
    }

    private fun workName(taskId: String): String = MANUAL_WORK_ID_PREFIX + taskId

    override suspend fun stopSyncTask(syncTask: SyncTask): Operation.State.SUCCESS {
        /*workManager.cancelUniqueWork(workName(syncTask.id)).state.observeForever {
            when (it) {
                is Operation.State.SUCCESS -> true
                is Operation.State.FAILURE -> false
            }
        }*/

        return workManager.cancelUniqueWork(workName(syncTask.id)).await()
    }

    companion object {
        const val TASK_ID = "TASK_ID"
        const val MANUAL_WORK_ID_PREFIX = "MANUAL-"
    }
}