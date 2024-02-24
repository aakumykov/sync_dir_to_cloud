package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.creator.SourceReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.factory_and_creator.TargetWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SyncTaskExecutor @Inject constructor(
    private val sourceReaderCreator: SourceReaderCreator,
    private val targetWriterCreator: TargetWriterCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncTaskNotificator: SyncTaskNotificator,
    private val syncObjectStateResetter: SyncObjectStateResetter,
    private val changesDetectionStrategy: ChangesDetectionStrategy.SizeAndModificationTime
) {
    private var sourceReader: SourceReader? = null
    private var mTargetWriter: TargetWriter? = null


    // FIXME: Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker. А как устойчивость к ошибкам?
    // TODO: переименовать в startExecutingTask()
    suspend fun executeSyncTask(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also {  syncTask ->
            prepareReader(syncTask)
            prepareWriter(syncTask)
            doWork(syncTask)
        }
    }


    suspend fun stopExecutingTask(taskId: String) {
        // TODO: по-настоящему прерывать работу CloudWriter-а
        MyLogger.d(TAG, "stopExecutingTask(), [${hashCode()}]")
        syncTaskStateChanger.changeExecutionState(taskId, SyncState.NEVER)
    }

    // FIXME: убрать !! в sourcePath
    private suspend fun doWork(syncTask: SyncTask) {

        val taskId = syncTask.id
        val notificationId = syncTask.notificationId

        try {
            syncTaskStateChanger.changeExecutionState(taskId, SyncState.RUNNING)

            prepareForSync(taskId)
            produceSync(syncTask)

            syncTaskStateChanger.changeExecutionState(taskId, SyncState.SUCCESS)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(taskId, SyncState.ERROR, ExceptionUtils.getErrorMessage(t))
        }
        finally {
            syncTaskNotificator.hideNotification(taskId, notificationId)
        }
    }

    private suspend fun prepareForSync(taskId: String) {
        syncObjectStateResetter.resetSyncObjectsStateOfTask(taskId)
    }

    private suspend fun produceSync(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.READING_SOURCE)
        sourceReader?.read(syncTask.sourcePath!!)

        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.WRITING_TARGET)
        mTargetWriter?.writeToTarget()
    }

    private fun prepareReader(syncTask: SyncTask) {
        // TODO: реализовать sourceAuthToken
        sourceReader = sourceReaderCreator.create(
            syncTask.sourceType,
            "",
            syncTask.id,
            changesDetectionStrategy
        )
    }

    private suspend fun prepareWriter(syncTask: SyncTask) {

        // FIXME: убрать двойное !!

        val targetAuthToken = cloudAuthReader.getCloudAuth(syncTask.cloudAuthId!!)?.authToken
            ?: throw IllegalStateException("Target auth token cannot be null")

        mTargetWriter = targetWriterCreator.create(
            syncTask.targetType!!,
            targetAuthToken,
            syncTask.id,
            syncTask.sourcePath!!,
            syncTask.targetPath!!
        )
    }


    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}