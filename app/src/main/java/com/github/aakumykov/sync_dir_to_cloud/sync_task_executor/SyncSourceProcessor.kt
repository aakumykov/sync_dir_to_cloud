package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncSourceProcessor @AssistedInject constructor(
    @Assisted private val sourceStorageType: StorageType,
    @Assisted private val sourceAuthToken: String,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncTaskFilesPreparerFactory: SyncTaskFilesPreparer.Factory
) {

    private val syncTaskFilesPreparer: SyncTaskFilesPreparer? by lazy {
        recursiveDirReaderFactory.create(sourceStorageType, sourceAuthToken)?.let { rdr ->
            syncTaskFilesPreparerFactory.create(rdr)
        }
    }

    suspend fun processSource(syncTask: SyncTask) {
        syncTaskFilesPreparer?.prepareSyncTask(syncTask)
    }

    @AssistedFactory
    interface Factory {
        fun create(sourceStorageType: StorageType, sourceAuthToken: String): SyncSourceProcessor
    }
}
