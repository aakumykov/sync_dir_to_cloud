package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.FullSyncTaskLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class FullSyncTaskRepository @Inject constructor(
    private val fullSyncTaskLocalDataSource: FullSyncTaskLocalDataSource,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher // FIXME: не нравится мне это здесь
)
    : FullSyncTaskReader, FullSyncTaskUpdater, FullSyncTaskCreatorDeleter
{
    override suspend fun createFullSyncTask(fullSyncTask: FullSyncTask) {
        fullSyncTaskLocalDataSource.addFullSyncTask(fullSyncTask)
    }

    override suspend fun deleteFullSyncTask(fullSyncTask: FullSyncTask) {
        fullSyncTaskLocalDataSource.deleteFullSyncTask(fullSyncTask)
    }

    override suspend fun getFullSyncTask(id: String): FullSyncTask {
        return fullSyncTaskLocalDataSource.getFullSyncTask(id)
    }

    override suspend fun updateFullSyncTask(fullSyncTask: FullSyncTask) {
        fullSyncTaskLocalDataSource.update(fullSyncTask)
    }
}