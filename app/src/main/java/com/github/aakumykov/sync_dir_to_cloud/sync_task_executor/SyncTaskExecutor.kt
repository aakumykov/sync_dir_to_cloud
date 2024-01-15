package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.creator.SourceReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.factory_and_creator.TargetWriterCreator
import javax.inject.Inject

class SyncTaskExecutor @Inject constructor(
    private val sourceReaderCreator: SourceReaderCreator,
    private val targetWriterCreator: TargetWriterCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncTaskNotificator: SyncTaskNotificator
) {
    private var sourceReader: SourceReader? = null
    private var mTargetWriter: TargetWriter? = null


    // Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker (а как устойчивость к ошибкам?)
    suspend fun executeSyncTask(syncTask: SyncTask) {
        prepareReaderAndWriter(syncTask)
        doWork(syncTask)
    }


    private suspend fun doWork(syncTask: SyncTask) {

        // FIXME: !! в sourcePath

        syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.READING_SOURCE)
        syncTaskNotificator.showNotification(syncTask.notificationId, SyncTask.State.READING_SOURCE)
        sourceReader?.read(syncTask.sourcePath!!)

        syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.WRITING_TARGET)
        syncTaskNotificator.showNotification(syncTask.notificationId, SyncTask.State.WRITING_TARGET)
        mTargetWriter?.writeToTarget()

        syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.SUCCESS)
        syncTaskNotificator.hideNotification(syncTask.notificationId)
    }


    private suspend fun prepareReaderAndWriter(syncTask: SyncTask) {

        val taskId = syncTask.id
        // TODO: --> targetAuthId
        val authId: String? = syncTask.cloudAuthId

        val sourceAuthToken = "" // TODO: реализовать

        // FIXME: убрать !!
        val targetAuthToken = cloudAuthReader.getCloudAuth(authId!!)?.authToken
            ?: throw IllegalStateException("Target auth token cannot be null")


        val sourceType: StorageType = syncTask.sourceType
            ?: throw IllegalStateException("Source type cannot be null")

        val targetType: StorageType = syncTask.targetType
            ?: throw IllegalStateException("Target type cannot be null")


        sourceReader = sourceReaderCreator.create(sourceType, sourceAuthToken, taskId)

        // FIXME: убрать targetPath!!
        mTargetWriter = targetWriterCreator.create(
            targetType,
            targetAuthToken,
            taskId,
            syncTask.sourcePath!!,
            syncTask.targetPath!!
        )
    }

    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}