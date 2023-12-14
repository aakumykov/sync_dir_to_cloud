package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthReader
import javax.inject.Inject

class SyncTaskExecutor @Inject constructor(
    private val sourceReaderFactory: SyncSourceReader.Factory,
    private val targetWriterFactory: SyncTargetWriter.Factory,
    private val notificator: SyncTaskNotificator,
    private val cloudAuthReader: CloudAuthReader
) {
    private var sourceReader: SyncSourceReader? = null
    private var targetWriter: SyncTargetWriter? = null

    suspend fun executeSyncTask(syncTask: SyncTask) {

        createObjectsFromFactories(syncTask)

        notificator.showNotification(syncTask)
         sourceReader?.readSource(syncTask)
         targetWriter?.writeToTarget(syncTask)
        notificator.hideNotification(syncTask)
    }

    // FIXME: убрать !!
    private suspend fun createObjectsFromFactories(syncTask: SyncTask) {
        val authToken = cloudAuthReader.getCloudAuth(syncTask.id).authToken
        sourceReader = sourceReaderFactory.create(StorageType.LOCAL, "")
        targetWriter = targetWriterFactory.create(syncTask.targetType!!, authToken)
    }
}