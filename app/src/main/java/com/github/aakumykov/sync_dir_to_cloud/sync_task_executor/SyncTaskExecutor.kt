package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class SyncTaskExecutor @Inject constructor(
    private val sourceProcessorFactory: SyncSourceProcessor.Factory,
    private val targetWriterFactory: SyncTargetWriter.Factory,
    private val cloudAuthReader: CloudAuthReader,
    private val notificator: SyncTaskNotificator
) {
    private var sourceProcessor: SyncSourceProcessor? = null
    private var targetWriter: SyncTargetWriter? = null

    suspend fun executeSyncTask(syncTask: SyncTask) {

        createObjectsFromFactories(syncTask)

        notificator.showNotification(syncTask)
         sourceProcessor?.processSource(syncTask)
         targetWriter?.writeToTarget(syncTask)
        notificator.hideNotification(syncTask)
    }

    // FIXME: нужен target auth token!
    // FIXME: убрать !!
    private suspend fun createObjectsFromFactories(syncTask: SyncTask) {
        syncTask.cloudAuthId?.let { cloudAuthId ->
            val cloudAuth = cloudAuthReader.getCloudAuth(cloudAuthId)
            val authToken: String? = cloudAuth?.authToken
            if (null != authToken) {
                sourceProcessor = sourceProcessorFactory.create(StorageType.LOCAL, "")
                targetWriter = targetWriterFactory.create(syncTask.targetType!!, authToken)
            }
        }
    }
}