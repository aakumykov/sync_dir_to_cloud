package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.di.authHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.in_target_existence_checker.InTargetExistenceChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_to_target_writer2.SyncObjectToTargetWriter2Creator
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

    private val changesDetectionStrategy: ChangesDetectionStrategy.SizeAndModificationTime, // FIXME: это не нужно передавать через конструктор

    private val syncObjectToTargetWriter2Creator: SyncObjectToTargetWriter2Creator,

    private val inTargetExistenceCheckerFactory: InTargetExistenceChecker.Factory
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

        showReadingSourceNotification(syncTask)

        try {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            prepareSync(syncTask.id)

            readSource2(syncTask)
//            readSource(syncTask)
            readTarget(syncTask)

            doSync(syncTask)
//            writeToTarget2(syncTask)


            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)
        }
        catch (t: Throwable) {
            ExceptionUtils.getErrorMessage(t).also { errorMsg ->
                syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(t))
                Log.e(TAG, errorMsg, t)
            }
        }
        finally {
            syncTaskNotificator.hideNotification(taskId, notificationId)
        }
    }

    private suspend fun prepareSync(taskId: String) {
        syncObjectStateResetter.markAllObjectsAsDeleted(taskId)
    }

    private suspend fun readSource2(syncTask: SyncTask) {
        appComponent
            .getStorageToDatabaseLister()
            .readFromPath(
                pathReadingFrom = syncTask.sourcePath,
                taskId = syncTask.id,
                cloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId),
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }

    private suspend fun writeToTarget2(syncTask: SyncTask) {
//        appComponent
//            .getDatabaseToStorageWriterOld()
//            .writeFromDatabaseToStorage(syncTask)

        appComponent
            .getDatabaseToStorageWriter()
            .writeFromDatabaseToStorage(syncTask)
    }

    private suspend fun readSource(syncTask: SyncTask) {
        readSourceReal(syncTask)
    }

    private suspend fun readSourceReal(syncTask: SyncTask) {
        storageReaderCreator.create(
            syncTask.sourceStorageType,
            authHolder.getSourceAuthToken(syncTask),
            syncTask.id,
            ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
        )
            ?.read(syncTask.sourcePath)
    }


    private suspend fun readTarget(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id).forEach { syncObject ->
            inTargetExistenceCheckerFactory.create(syncTask).checkObjectExists(syncObject)
        }
    }


    private suspend fun doSync(syncTask: SyncTask) {

        copyNewItems(syncTask)
        copyModifiedItems(syncTask)

        copyInTargetMissingItems(syncTask)
        copyNeverSyncedItems(syncTask)
        copyErrorItems(syncTask)
    }

    private suspend fun resetBadSyncStates(syncTask: SyncTask) {
        syncObjectStateResetter.markBadStatesAsNeverSynced(syncTask.id)
    }


    private suspend fun copyNewItems(syncTask: SyncTask) {
        copyItemsWithModificationState(syncTask, ModificationState.NEW)
    }

    private suspend fun copyModifiedItems(syncTask: SyncTask) {
        copyItemsWithModificationState(syncTask, ModificationState.MODIFIED)
    }


    private suspend fun copyInTargetMissingItems(syncTask: SyncTask) {
        syncObjectReader.getInTargetMissingObjects(syncTask.id)
            .let { it }
            .forEach { syncObject ->
                syncObjectToTargetWriter2Creator.create(syncTask)?.write(syncTask, syncObject, true)
            }
    }

    private suspend fun copyNeverSyncedItems(syncTask: SyncTask) {
        syncObjectReader.getObjectsForTaskWithSyncState(syncTask.id, ExecutionState.NEVER)
//            .let { it }
            .forEach { syncObject ->
                syncObjectToTargetWriter2Creator.create(syncTask)?.write(syncTask, syncObject, true)
            }
    }


    private suspend fun copyErrorItems(syncTask: SyncTask) {
        syncObjectReader.getObjectsForTaskWithSyncState(syncTask.id, ExecutionState.ERROR)
//            .let { it }
            .forEach { syncObject ->
                syncObjectToTargetWriter2Creator.create(syncTask)?.write(syncTask, syncObject, true)
            }
    }


    private suspend fun copyItemsWithModificationState(syncTask: SyncTask, modificationState: ModificationState) {
        syncObjectReader.getObjectsForTaskWithModificationState(syncTask.id, modificationState)
            /*.let {
                val ms = modificationState
                it
            }*/
            .forEach { syncObject ->
                syncObjectToTargetWriter2Creator.create(syncTask)
                    ?.write(syncTask, syncObject, true)
        }
    }



    private fun showWritingTargetNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.WRITING_TARGET)
    }

    private fun showReadingSourceNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.READING_SOURCE)
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