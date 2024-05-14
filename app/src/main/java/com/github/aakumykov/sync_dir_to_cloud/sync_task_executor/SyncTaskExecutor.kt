package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierCreator
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
    private val syncObjectDeleter: SyncObjectDeleter,
    private val syncObjectStateResetter: SyncObjectStateResetter,
    private val changesDetectionStrategy: ChangesDetectionStrategy.SizeAndModificationTime,
    private val sourceFileStreamSupplierCreator: SourceFileStreamSupplierCreator
) {
    private var sourceReader: SourceReader? = null
    private var targetWriter: TargetWriter? = null

    // FIXME: Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker. Как устойчивость к ошибкам?
    suspend fun executeSyncTask(taskId: String) {

        MyLogger.d(TAG, "executeSyncTask() [${classNameWithHash()}]")

        syncTaskReader.getSyncTask(taskId).also {  syncTask ->
            prepareReader(syncTask)
            prepareWriter(syncTask)
            doWork(syncTask)
        }
    }


    suspend fun stopExecutingTask(taskId: String) {
        // TODO: по-настоящему прерывать работу CloudWriter-а
        MyLogger.d(TAG, "stopExecutingTask(), [${hashCode()}]")
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.NEVER)
    }

    // FIXME: убрать !! в sourcePath
    private suspend fun doWork(syncTask: SyncTask) {

        val taskId = syncTask.id
        val notificationId = syncTask.notificationId

        try {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            // Стираю из БД объекты, удалённые в предыдущую синхронизацию
            // (чтобы не пытаться удалить их повторно).
            removePreviouslyDeletedObjects(taskId)

            // Меняю ExecutionState.RUNNING на ExecutionState.NEVER, чтобы эти объекты синхронизировались вновь
            // (такой статус может сохраниться, если приложение было убито во время работы).
            markBadStatesAsNeverSynced(taskId)

            // Помечаю всё объекты в БД как удалённые, чтобы не проверять каждый раз существование
            // связанного файла/папки. Те, что существуют, будут обнаружены на этапе чтения источника.
            markObjectsVirtuallyDeleted(taskId)


            showReadingSourceNotification(syncTask)
            readFromSourceToDatabase(syncTask)


            // TODO: уведомление
            checkTargetFilesExistence()


            showWritingTargetNotification(syncTask)
            writeFromDatabaseToTarget(syncTask)


            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(t))
        }
        finally {
            syncTaskNotificator.hideNotification(taskId, notificationId)
        }
    }

    private fun showWritingTargetNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.WRITING_TARGET)
    }

    private fun showReadingSourceNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.READING_SOURCE)
    }

    private suspend fun removePreviouslyDeletedObjects(taskId: String) {
        syncObjectDeleter.clearObjectsWasSuccessfullyDeleted(taskId)
    }

    private suspend fun markObjectsVirtuallyDeleted(taskId: String) {
        syncObjectStateResetter.markAllObjectsAsDeleted(taskId)
    }


    private suspend fun readFromSourceToDatabase(syncTask: SyncTask) {
        sourceReader?.read(syncTask.sourcePath!!)
    }


    private suspend fun checkTargetFilesExistence(syncTask: SyncTask) {
        syncTask.targetAuthId?.also { targetAuthId ->
            cloudAuthReader.getCloudAuth(targetAuthId)?.also { targetCloudAuth ->
                syncTask.targetStorageType?.also { targetStorageType ->
                    sourceReaderCreator.create(
                        targetStorageType,
                        targetCloudAuth.authToken,
                        syncTask.id,
                        ChangesDetectionStrategy.SizeAndModificationTime()
                    )?.also { targetReader ->

                    }
                }
            }
        }
    }


    private suspend fun writeFromDatabaseToTarget(syncTask: SyncTask) {
        syncTask.sourceAuthId?.also { sourceAuthId ->
            cloudAuthReader.getCloudAuth(sourceAuthId)?.also { cloudAuth ->
                syncTask.sourceStorageType?.also { sourceStorageType ->
                    sourceFileStreamSupplierCreator.create(syncTask.id, sourceStorageType)?.also { sourceFileStreamSupplier ->
                        targetWriter?.writeToTarget(sourceFileStreamSupplier, true)
                    }
                }
            }
        }
    }


    private suspend fun markBadStatesAsNeverSynced(taskId: String) {
        syncObjectStateResetter.markBadStatesAsNeverSynced(taskId)
    }


    private suspend fun prepareReader(syncTask: SyncTask) {
        syncTask.sourceAuthId?.also { sourceAuthId ->
            cloudAuthReader.getCloudAuth(sourceAuthId)?.also { sourceAuth ->
                sourceReader = sourceReaderCreator.create(
                    syncTask.sourceStorageType,
                    sourceAuth.authToken,
                    syncTask.id,
                    changesDetectionStrategy
                )
            }
        }
    }

    private suspend fun prepareWriter(syncTask: SyncTask) {
        syncTask.targetAuthId?.also { targetAuthId ->
            cloudAuthReader.getCloudAuth(targetAuthId)?.also { targetAuth ->
                targetWriter = targetWriterCreator.create(
                    syncTask.targetStorageType!!,
                    targetAuth.authToken,
                    syncTask.id,
                    syncTask.sourcePath!!,
                    syncTask.targetPath!!
                )
            }
        }
    }


    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}