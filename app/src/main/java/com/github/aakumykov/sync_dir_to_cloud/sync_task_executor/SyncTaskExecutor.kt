package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.di.factories.SyncTaskFilesPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class SyncTaskExecutor @Inject constructor(
    private val syncTaskFilesPreparerFactory: SyncTaskFilesPreparerAssistedFactory,
    private val syncTaskFilesUploader: SyncTaskFilesUploader,
    private val syncTaskExecutionNotificator: SyncTaskExecutionNotificator
) {
    suspend fun executeSyncTask(syncTask: SyncTask) {

        val recursiveDirReader = App.getAppComponent().getRecursiveDirReaderFactory().create(StorageType.LOCAL, syncTask.cloudAuthId!!)
        val syncTaskPreparer = syncTaskFilesPreparerFactory.create(recursiveDirReader!!)

        syncTaskExecutionNotificator.showNotification(syncTask)
         syncTaskPreparer.prepareSyncTask(syncTask)
         syncTaskFilesUploader.uploadFiles(syncTask)
        syncTaskExecutionNotificator.hideNotification(syncTask)
    }
}