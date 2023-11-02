package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import javax.inject.Inject

@AppScope
class SyncTaskManagingUseCase @Inject constructor(
    val syncTaskReader: SyncTaskReader,
    val syncTaskCreatorDeleter: SyncTaskCreatorDeleter,
    val syncTaskUpdater: SyncTaskUpdater,
    val fullSyncTaskReader: FullSyncTaskReader,
    val fullSyncTaskCreatorDeleter: FullSyncTaskCreatorDeleter,
    val fullSyncTaskUpdater: FullSyncTaskUpdater
) {

    suspend fun listSyncTasks(): LiveData<List<SyncTask>>
        = syncTaskReader.listSyncTasks()


    suspend fun getSyncTask(id: String): SyncTask
        = syncTaskReader.getSyncTask(id)


    suspend fun deleteSyncTask(syncTask: SyncTask)
        = syncTaskCreatorDeleter.deleteSyncTask(syncTask)


    suspend fun createOrUpdateSyncTask(syncTask: SyncTask) {
        if (null != syncTaskReader.getSyncTask(syncTask.id))
            syncTaskUpdater.updateSyncTask(syncTask)
        else
            syncTaskCreatorDeleter.createSyncTask(syncTask)
    }


    suspend fun getFullSyncTask(id: String)
        = fullSyncTaskReader.getFullSyncTask(id)

    suspend fun createOfUpdateFullSyncTask(fullSyncTask: FullSyncTask) {
        if (null == fullSyncTask.syncTask.id)
            fullSyncTaskUpdater.updateFullSyncTask(fullSyncTask)
        else
            fullSyncTaskCreatorDeleter.createFullSyncTask(fullSyncTask)
    }
}