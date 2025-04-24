package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.DirsBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.SyncTaskFilesCopier
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs.SyncTaskDirsCreator
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskStateLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_to_target_writer2.SyncObjectToTargetWriter2Creator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.creator.StorageReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces.StorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.SizeAndModificationTimeChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.StorageWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.SyncTaskLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.resume

/*
FIXME: отображается прогресс только в первой порции копируемых файлов.
 Те, что были за пределами операции, идут на следующий этап - "копирование
 забытых файлов".
 */

/*
FIXME: всё-таки, происходит смешение операций!
*/

/*
FIXME: что, если удалённо файл пропал, а локально изменился?
 */

/*
FIXME: удалённо пропал и локально пропал...
 */

class SyncTaskExecutor @AssistedInject constructor(

    @Assisted private val coroutineScope: CoroutineScope,

    private val storageReaderCreator: StorageReaderCreator,

    private val cloudAuthReader: CloudAuthReader,

    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncTaskNotificator: SyncTaskNotificator,

    private val syncTaskLogger: SyncTaskLogger,
    private val taskStateLogger: TaskStateLogger,
    private val executionLogger: ExecutionLogger,

    private val resources: Resources,

    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateResetter: SyncObjectStateResetter,

    private val changesDetectionStrategy: SizeAndModificationTimeChangesDetectionStrategy, // FIXME: это не нужно передавать через конструктор

    private val syncObjectToTargetWriter2Creator: SyncObjectToTargetWriter2Creator,
) {
    private val executionId: String get() = hashCode().toString()

    private var currentTask: SyncTask? = null
    private var storageReader: StorageReader? = null

    @Deprecated("Не используется")
    private var storageWriter: StorageWriter? = null

    private val dirsBackuperCreator: DirsBackuperCreator by lazy { appComponent.getDirsBackuperCreator() }
    private val filesBackuperCreator: FilesBackuperCreator by lazy { appComponent.getFilesBackuperCreator() }
    private val syncTaskDirCreator: SyncTaskDirsCreator by lazy { appComponent.getSyncTaskDirsCreatorAssistedFactory().create(executionId) }
    private val syncTaskFilesCopier: SyncTaskFilesCopier by lazy { appComponent.getSyncTaskFilesCopierAssistedFactory().create(executionId) }
    private val syncTaskRunningTimeUpdater: SyncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }

    // FIXME: Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker. Как устойчивость к ошибкам?
    suspend fun executeSyncTask(taskId: String) {

        Log.d(TAG, "")
        Log.d(TAG, "")
        Log.d(tag, "========= executeSyncTask() [${classNameWithHash()}] СТАРТ ========")

        syncTaskReader.getSyncTask(taskId).also {  syncTask ->
            currentTask = syncTask
            prepareReader(syncTask)
            doWork(syncTask)
        }

        Log.d(tag, "========= executeSyncTask() [${classNameWithHash()}] ФИНИШ ========")
    }

    /**
     * Важно запускать этот класс в режиме один экземпляр - одна задача (SyncTask).
     * Иначе будут сбрабываться статусы уже выполняющихся задач (!)
     */
    private suspend fun doWork(syncTask: SyncTask) {

        val taskId = syncTask.id
        val notificationId = syncTask.notificationId

//        showReadingSourceNotification(syncTask)

        logExecutionStart(syncTask, executionId)

        try {
            syncTaskRunningTimeUpdater.updateStartTime(taskId)

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            // Удалить выполненные инструкции
            deleteProcessedSyncInstructions(syncTask)

            // Выполнить недоделанные инструкции
            removeDuplicatedUnprocessedSyncInstructions(syncTask)
            processUnprocessedSyncInstructions(syncTask)

            // Выполнить подготовку
            resetTaskBadStates(taskId)
            resetObjectsBadState(taskId)

            markAllObjectsAsNotChecked(taskId)

            // Прочитать источник
            readSource(syncTask).getOrThrow()

            // Прочитать приёмник
            readTarget(syncTask)

            // Отметить все не найденные объекты как удалённые
            markAllNotCheckedObjectsAsDeleted(taskId)

            deleteOldComparisonStates(syncTask)
            compareSourceWithTarget(syncTask)

            generateSyncInstructions(syncTask)

            processSyncInstructions(syncTask)

            clearProcessedSyncObjectsWithDeletedState(syncTask)

            // Сравнить источник с приёмником
//            compareSourceWithTarget(syncTask.id)

//            copyFilesProbe(syncTask).join()

            // Выполнить инструкции синхронизации
//            processSyncInstructions(syncTask)

            // Забэкапить удалённое
//            backupDeletedDirs(syncTask)
//            backupDeletedFiles(syncTask)

            // Забэкапить изменившееся
//            backupModifiedFiles(syncTask)

            // Удалить удалённые файлы
//            deleteDeletedFiles(syncTask) // Выполнять перед удалением каталогов

            // Удалить удалённые каталоги
//            deleteDeletedDirs(syncTask) // Выполнять после удаления файлов

            // TODO: очистка БД от удалённых элементов как отдельный этап?

            // Создать новые каталоги, восстановить утраченные (перед копированием файлов)
//            createNewDirs(syncTask)
//            createLostDirsAgain(syncTask)

            // Создать никогда не создававшиеся каталоги (перед файлами) ЛОГИЧНЕЕ ПОСЛЕ ФАЙЛОВ ИНАЧЕ НОВЫЕ ОБРАБАТЫВАЮТСЯ КАК ...
//            createNeverSyncedDirs(syncTask)

            // Скопировать новые файлы
//            copyNewFiles(syncTask)?.join()

            // Скопировать забытые с прошлого раза файлы
//            copyPreviouslyForgottenFiles(syncTask)?.join()

            // Скопировать изменившееся
//            copyModifiedFiles(syncTask)?.join()

            // Восстановить утраченные файлы
//            copyLostFilesAgain(syncTask)?.join()

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)

            logExecutionFinish(syncTask)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, t.errorMsg)
            Log.e(TAG, t.errorMsg, t)
            logExecutionError(syncTask, t)
        }
        finally {
//            syncTaskNotificator.hideNotification(taskId, notificationId)
            syncTaskRunningTimeUpdater.updateFinishTime(taskId)
        }
    }

    private suspend fun removeDuplicatedUnprocessedSyncInstructions(syncTask: SyncTask) {
        appComponent
            .getSyncInstructionRepository6()
            .deleteUnprocessedDuplicatedInstructions(syncTask.id)
    }

    private suspend fun clearProcessedSyncObjectsWithDeletedState(syncTask: SyncTask) {
        appComponent
            .getSyncObjectDeleter()
            .deleteProcessedObjectsWithDeletedState(syncTask.id)
    }

    private suspend fun markAllNotCheckedObjectsAsDeleted(taskId: String) {
        syncObjectStateResetter.markAllNotCheckedObjectsAsDeleted(taskId)
    }

    private suspend fun deleteOldComparisonStates(syncTask: SyncTask) {
        appComponent
            .getComparisonsDeleter6()
            .deleteAllFor(syncTask.id)
    }

    private suspend fun deleteProcessedSyncInstructions(syncTask: SyncTask) {
        appComponent
            .getInstructionsDeleter6()
            .deleteFinishedInstructionsFor(syncTask.id)
    }

    private suspend fun generateSyncInstructions(syncTask: SyncTask) {
        appComponent
            .getInstructionsGeneratorAssistedFactory6()
            .create(syncTask,executionId)
            .generate()
    }


    @Throws(Exception::class)
    private suspend fun copyFilesProbe(syncTask: SyncTask): Job {
        return appComponent
            .getProbeFilesCopier()
            .copyFiles(syncTask, coroutineScope)

//        return qwertyScope(coroutineScope)
    }

    private fun qwertyScope(scope: CoroutineScope): Job {
        return scope.launch {
            try {
                qwerty()
            } catch (e: CancellationException) {
                Log.e(TAG, e.errorMsg)
            }
        }
    }

    private suspend fun qwerty() {
        return suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation {
                Log.d(TAG, it?.errorMsg ?: "")
            }
            thread {
                repeat(5) { i->
                    if (!cont.isActive)
                        return@repeat
                    Log.d(TAG, "qwerty(), $i")
                    TimeUnit.SECONDS.sleep(1)
                }
                cont.resume(Unit)
            }
        }
    }


    private suspend fun processUnprocessedSyncInstructions(syncTask: SyncTask) {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory6()
            .create(syncTask, executionId, coroutineScope)
            .processUnprocessedInstructions()
    }


    private suspend fun processSyncInstructions(syncTask: SyncTask) {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory6()
            .create(syncTask, executionId, coroutineScope)
            .processCurrentInstructions()
    }


    private suspend fun logExecutionStart(syncTask: SyncTask, executionId: String) {

        executionLogger.log(ExecutionLogItem.createFinishingItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_begins)
        ))

        taskStateLogger.logRunning(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = syncTask.id,
            entryType = ExecutionLogItemType.START
        ))
    }

    private suspend fun logExecutionFinish(syncTask: SyncTask) {

        executionLogger.log(ExecutionLogItem.createFinishingItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_ends)
        ))

        taskStateLogger.logSuccess(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = syncTask.id,
            entryType = ExecutionLogItemType.FINISH
        ))
    }

    private suspend fun logExecutionError(syncTask: SyncTask, t: Throwable) {

        executionLogger.log(ExecutionLogItem.createErrorItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_error)
        ))

        taskStateLogger.logError(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = syncTask.id,
            entryType = ExecutionLogItemType.ERROR,
            errorMsg = null
        ))
    }


    // TODO: регистрировать ошибку?
    private suspend fun deleteDeletedDirs(syncTask: SyncTask) {
        appComponent
            .getTaskDirDeleterCreator()
            .createTaskDirsDeleterForTask(syncTask, executionId)
            ?.deleteDeletedDirsForTask(syncTask.id)
            ?: {
                Log.e(TAG, "Не удалось создать удалятель каталогов для задачи ${syncTask.description}")
            }
    }

    private suspend fun deleteDeletedFiles(syncTask: SyncTask) {
        appComponent
            .getTaskFilesDeleterCreator()
            .createDeletedFilesDeleterForTask(syncTask, executionId)
            ?.deleteDeletedFilesForTask(syncTask)
            ?: {
                Log.e(TAG, "Не удалось создать удалятель файлов для задачи ${syncTask.description}")
            }
    }

    private suspend fun backupDeletedDirs(syncTask: SyncTask) {
        dirsBackuperCreator
            .createDirsBackuperForTask(syncTask, executionId)
            ?.backupDeletedDirsOfTask(syncTask)
            ?: { Log.e(TAG, "Не удалось создать бэкапер каталогов для задачи ${syncTask.description}") }
    }

    private suspend fun backupDeletedFiles(syncTask: SyncTask) {
        filesBackuperCreator
            .createFilesBackuperForSyncTask(syncTask, executionId)
            ?.backupDeletedFilesOfTask(syncTask)
            ?: { Log.e(TAG, "Не удалось создать бэкапер для удалённых файлов для задачи ${syncTask.description}") }
    }

    private suspend fun backupModifiedFiles(syncTask: SyncTask) {
        filesBackuperCreator
            .createFilesBackuperForSyncTask(syncTask, executionId)
            ?.backupModifiedFilesOfTask(syncTask)
            ?: { Log.e(TAG, "Не удалось создать бэкапер для изменившихся файлов для задачи ${syncTask.description}") }
    }

    private suspend fun createLostDirsAgain(syncTask: SyncTask) {
        syncTaskDirCreator.createInTargetLostDirs(syncTask)
    }

    private suspend fun copyLostFilesAgain(syncTask: SyncTask): Job? {
        return syncTaskFilesCopier.copyInTargetLostFiles(
            syncTask = syncTask,
            scope = coroutineScope,
        )
    }

    private suspend fun createNeverSyncedDirs(syncTask: SyncTask) {
        syncTaskDirCreator.createNeverProcessedDirs(syncTask)
    }

    private suspend fun copyPreviouslyForgottenFiles(syncTask: SyncTask): Job? {
        return syncTaskFilesCopier.copyPreviouslyForgottenFilesInCoroutine(
            syncTask = syncTask,
            scope = coroutineScope,
        )
    }

    private suspend fun copyModifiedFiles(syncTask: SyncTask): Job? {
        return syncTaskFilesCopier.copyModifiedFilesForSyncTask(
            syncTask = syncTask,
            scope = coroutineScope,
        )
    }

    private suspend fun copyNewFiles(syncTask: SyncTask): Job? {
        return syncTaskFilesCopier.copyNewFilesForSyncTaskInCoroutine(
            syncTask = syncTask,
            scope = coroutineScope,
        )
    }

    private suspend fun createNewDirs(syncTask: SyncTask) {
        syncTaskDirCreator.createNewDirs(syncTask)
    }


    private suspend fun resetTaskBadStates(taskId: String) {
        syncTaskStateChanger.resetSourceReadingBadState(taskId)
    }

    private suspend fun resetObjectsBadState(taskId: String) {
        syncObjectStateResetter.resetTargetReadingBadState(taskId)
        syncObjectStateResetter.resetBackupBadState(taskId)
        syncObjectStateResetter.resetBackupBadState(taskId)
        syncObjectStateResetter.resetDeletionBadState(taskId)
        syncObjectStateResetter.resetSyncBadState(taskId)
    }


    private suspend fun markAllObjectsAsNotChecked(taskId: String) {
        syncObjectStateResetter.markAllObjectsAsNotChecked(taskId)
    }


    /**
     * @return Флаг успешности чтения источника.
     */
    private suspend fun readSource(syncTask: SyncTask): Result<Boolean> {
        return appComponent
            .getStorageToDatabaseLister()
            .listFromPathToDatabase(
                syncSide = SyncSide.SOURCE,
                taskId = syncTask.id,
                executionId = executionId,
                cloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId!!),
                pathReadingFrom = syncTask.sourcePath,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    private suspend fun readTarget(syncTask: SyncTask) {
        appComponent
            .getStorageToDatabaseLister()
            .listFromPathToDatabase(
                syncSide = SyncSide.TARGET,
                taskId = syncTask.id,
                executionId = executionId,
                cloudAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!),
                pathReadingFrom = syncTask.targetPath,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    private suspend fun compareSourceWithTarget(syncTask: SyncTask) {
        appComponent
            .getSourceWithTargetComparatorAssistedFactory5()
            .create(syncTask = syncTask, executionId = executionId)
            .apply {
                compareSourceWithTarget()
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


    suspend fun stopExecutingTask(taskId: String) {
        // TODO: по-настоящему прерывать работу CloudWriterGetter-а
        MyLogger.d(tag, "stopExecutingTask(), [${hashCode()}]")
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.NEVER)
    }


    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}