package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import javax.inject.Inject

@AppScope
class SyncTaskManagingUseCase @Inject constructor(
    val syncTaskReader: SyncTaskReader,
    val syncTaskCreatorDeleter: SyncTaskCreatorDeleter,
    val syncTaskUpdater: SyncTaskUpdater
) {

    suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
        return syncTaskReader.listSyncTasks()
    }


    suspend fun getSyncTask(id: String): SyncTask {
        return syncTaskReader.getSyncTask(id)
    }


    suspend fun deleteSyncTask(syncTask: SyncTask) {
        syncTaskCreatorDeleter.deleteSyncTask(syncTask)
    }


    suspend fun createOrUpdateSyncTask(syncTask: SyncTask) {
        if (null != syncTaskReader.getSyncTask(syncTask.id))
            syncTaskUpdater.updateSyncTask(syncTask)
        else
            syncTaskCreatorDeleter.createSyncTask(syncTask)
    }
}