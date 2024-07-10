package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
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

            // Выполнить подготовку
            resetTaskBadStates(taskId)
            resetObjectsBadState(taskId)
            markAllObjectsAsDeleted(taskId)

            // Прочитать источник
            readSource(syncTask)

            // Прочитать приёмник
            readTarget(syncTask)

            // Забэкапить удалённое
            backupDeletedDirs(syncTask)
            backupDeletedFiles(syncTask)

            // Забэкапить изменившееся
//            backupModifiedItems(syncTask)

            // Удалить удалённые файлы
//            deleteDeletedFiles(syncTask) // Выполнять перед удалением каталогов

            // Удалить удалённые каталоги
//            deleteDeletedDirs(syncTask) // Выполнять после удаления файлов

            // TODO: очистка БД от удалённых элементов как отдельный этап?

            // Восстановить утраченные каталоги (перед копированием файлов!)
//            createLostDirsAgain(syncTask)

            // Создать никогда не создававшиеся каталоги (перед файлами)
            // TODO: выдавать сообщение
//            createNeverSyncedDirs(syncTask)

            // Скопировать не копировавшиеся файлы
//            copyNeverSyncedFiles(syncTask)

            // Скопировать новое
//            createNewDirs(syncTask)
//            copyNewFiles(syncTask)

            // Скопировать изменившееся
//            copyModifiedFiles(syncTask)

            // Восстановить утраченные файлы
//            copyLostFilesAgain(syncTask)

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

    // TODO: регистрировать ошибку?
    private suspend fun deleteDeletedDirs(syncTask: SyncTask) {
        appComponent
            .getTaskDirDeleterCreator()
            .createTaskDirsDeleterForTask(syncTask)
            ?.deleteDeletedDirsForTask(syncTask.id)
            ?: {
                Log.e(TAG, "Не удалось создать удалятель каталогов для задачи ${syncTask.description}")
            }
    }

    private suspend fun deleteDeletedFiles(syncTask: SyncTask) {
        appComponent
            .getTaskFilesDeleterCreator()
            .createDeletedFilesDeleterForTask(syncTask)
            ?.deleteDeletedFilesForTask(syncTask)
            ?: {
                Log.e(TAG, "Не удалось создать удалятель файлов для задачи ${syncTask.description}")
            }
    }

    private suspend fun backupDeletedDirs(syncTask: SyncTask) {
        appComponent
            .getDirsBackuperCreator()
            .createDirsBackuperForTask(syncTask)
            ?.backupDeletedDirsOfTask(syncTask)
            ?: {
                Log.e(TAG, "Не удалось создать бэкапер каталогов для задачи ${syncTask.description}")
            }
    }

    private suspend fun backupDeletedFiles(syncTask: SyncTask) {
        appComponent
            .getFilesBackuperCreator()
            .createFilesBackuperForSyncTask(syncTask)
            ?.backupDeletedFilesOfTask(syncTask)
            ?: {
                Log.e(TAG, "Не удалось создать бэкапер для удалённых файлов для задачи ${syncTask.description}")
            }
    }

    private suspend fun backupModifiedItems(syncTask: SyncTask) {
        appComponent
            .getFilesBackuperCreator()
            .createFilesBackuperForSyncTask(syncTask)
            ?.backupModifiedFilesOfTask(syncTask)
            ?: {
                Log.e(TAG, "Не удалось создать бэкапер для изменившихся файлов для задачи ${syncTask.description}")
            }
    }

    private suspend fun createLostDirsAgain(syncTask: SyncTask) {
        appComponent
            .getSyncTaskDirsCreator()
            .createInTargetLostDirs(syncTask)
    }

    private suspend fun copyLostFilesAgain(syncTask: SyncTask) {
        appComponent
            .getSyncTaskFilesCopier()
            .copyInTargetLostFiles(syncTask)
    }

    private suspend fun createNeverSyncedDirs(syncTask: SyncTask) {
        appComponent
            .getSyncTaskDirsCreator()
            .createNeverProcessedDirsFromTask(syncTask)
    }

    private suspend fun copyNeverSyncedFiles(syncTask: SyncTask) {
        appComponent
            .getSyncTaskFilesCopier()
            .copyNeverCopiedFilesOfSyncTask(syncTask)
    }

    private suspend fun copyModifiedFiles(syncTask: SyncTask) {
        appComponent
            .getSyncTaskFilesCopier()
            .copyModifiedFilesForSyncTask(syncTask)
    }

    private suspend fun copyNewFiles(syncTask: SyncTask) {
        appComponent
            .getSyncTaskFilesCopier()
            .copyNewFilesForSyncTask(syncTask)
    }

    private suspend fun createNewDirs(syncTask: SyncTask) {
        appComponent
            .getSyncTaskDirsCreator()
            .createNewDirsFromTask(syncTask)
    }


    private suspend fun resetTaskBadStates(taskId: String) {
        syncTaskStateChanger.resetSourceReadingBadState(taskId)
    }

    private suspend fun resetObjectsBadState(taskId: String) {
        syncObjectStateResetter.resetTargetReadingBadState(taskId)
        syncObjectStateResetter.resetBackupBadState(taskId)
        syncObjectStateResetter.resetBackupBadState(taskId)
        syncObjectStateResetter.resetDeletionBadState(taskId)

    }


    private suspend fun markAllObjectsAsDeleted(taskId: String) {
        syncObjectStateResetter.markAllObjectsAsDeleted(taskId)
    }


    private suspend fun readSource(syncTask: SyncTask) {
        appComponent
            .getStorageToDatabaseLister()
            .readFromPath(
                pathReadingFrom = syncTask.sourcePath,
                taskId = syncTask.id,
                cloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId),
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    private suspend fun readTarget(syncTask: SyncTask) {
        // TODO: вынести в отдельный класс по примеру других aa_v2-методов
        syncObjectReader.getAllObjectsForTask(syncTask.id).forEach { syncObject ->
            inTargetExistenceCheckerFactory
                .create(syncTask)
                .checkObjectExists(syncObject)
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