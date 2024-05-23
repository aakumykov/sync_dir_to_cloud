package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.di.authHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierCreator
import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.SyncObjectsToStorageWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.creator.StorageReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces.StorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.StorageWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.factory_and_creator.StorageWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SyncTaskExecutor @Inject constructor(
    private val storageReaderCreator: StorageReaderCreator,
    private val storageWriterCreator: StorageWriterCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncTaskNotificator: SyncTaskNotificator,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateResetter: SyncObjectStateResetter,
    private val changesDetectionStrategy: ChangesDetectionStrategy.SizeAndModificationTime,
    private val syncObjectsToStorageWriterCreator: SyncObjectsToStorageWriter.Creator,
    private val sourceFileStreamSupplierCreator: SourceFileStreamSupplierCreator,
) {
    private var currentTask: SyncTask? = null
    private var storageReader: StorageReader? = null
    private var storageWriter: StorageWriter? = null

    // FIXME: Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker. Как устойчивость к ошибкам?
    suspend fun executeSyncTask(taskId: String) {

        MyLogger.d(tag, "executeSyncTask() [${classNameWithHash()}]")

        syncTaskReader.getSyncTask(taskId).also {  syncTask ->
            currentTask = syncTask
            prepareReader(syncTask)
            prepareWriter(syncTask)
            doWork(syncTask)
        }
    }


    private suspend fun doWork(syncTask: SyncTask) {

        val taskId = syncTask.id
        val notificationId = syncTask.notificationId

        try {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            readSource(syncTask)
            readTarget(syncTask)
            syncSourceWithTarget(syncTask)

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(t))
        }
        finally {
            syncTaskNotificator.hideNotification(taskId, notificationId)
        }
    }

    private suspend fun readSource(syncTask: SyncTask) {
        markObjectsAsDeleted(StorageHalf.SOURCE, syncTask.id)
        readSourceReal(syncTask)
    }

    private suspend fun readSourceReal(syncTask: SyncTask) {
        storageReaderCreator.create(
            syncTask.sourceStorageType,
            authHolder.getSourceAuthToken(syncTask),
            syncTask.id,
            ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
        )
            ?.read(StorageHalf.SOURCE, syncTask.sourcePath)
    }


    private suspend fun readTarget(syncTask: SyncTask) {
        markObjectsAsDeleted(StorageHalf.TARGET, syncTask.id)
        readTargetReal(syncTask)
    }

    private suspend fun readTargetReal(syncTask: SyncTask) {
        storageReaderCreator.create(
            syncTask.targetStorageType,
            authHolder.getTargetAuthToken(syncTask),
            syncTask.id,
            ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
        )?.read(StorageHalf.TARGET, syncTask.targetPath)
    }


    private suspend fun syncSourceWithTarget(syncTask: SyncTask) {

        // FIXME: класс-писатель не должен читать
        /*storageWriter?.write(
            sourceFileStreamSupplier(syncTask.id, syncTask.sourceStorageType!!),
            ReadingStrategy.DEFAULT,
            true
        )*/

        // Выбрать объекты для синхронизации
        val objectListToSync: List<SyncObject> = syncObjectReader.getList(
            syncTask.id,
            StorageHalf.SOURCE,
            ReadingStrategy.Default()
        )

        syncObjectsToStorageWriterCreator.create(
            authHolder.getTargetAuthToken(syncTask),
            syncTask.targetStorageType
        ).writeObjectsToTarget(objectListToSync, syncTask)
    }


    private fun showWritingTargetNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.WRITING_TARGET)
    }

    private fun showReadingSourceNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.READING_SOURCE)
    }

    private suspend fun markObjectsAsDeleted(storageHalf: StorageHalf, taskId: String) {
        syncObjectStateResetter.markAllObjectsAsDeleted(storageHalf, taskId)
    }


    private suspend fun markBadStatesAsNeverSynced(taskId: String) {
        syncObjectStateResetter.markBadStatesAsNeverSynced(taskId)
    }


    private suspend fun prepareReader(syncTask: SyncTask) {
        syncTask.sourceAuthId?.also { sourceAuthId ->
            cloudAuthReader.getCloudAuth(sourceAuthId)?.also { sourceCloudAuth ->
                storageReader = storageReaderCreator.create(
                    syncTask.sourceStorageType,
                    sourceCloudAuth.authToken,
                    syncTask.id,
                    changesDetectionStrategy
                )
            }
        }
    }

    private suspend fun prepareWriter(syncTask: SyncTask) {
        syncTask.targetAuthId?.also { targetAuthId ->
            cloudAuthReader.getCloudAuth(targetAuthId)?.also { targetCloudAuth ->
                storageWriter = storageWriterCreator.create(
                    syncTask.targetStorageType!!,
                    targetCloudAuth.authToken,
                    syncTask.id,
                    syncTask.sourcePath!!,
                    syncTask.targetPath!!
                )
            }
        }
    }


    suspend fun stopExecutingTask(taskId: String) {
        // TODO: по-настоящему прерывать работу CloudWriter-а
        MyLogger.d(tag, "stopExecutingTask(), [${hashCode()}]")
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.NEVER)
    }


    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}