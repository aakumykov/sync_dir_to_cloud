package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseLister
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseListerAssistedFactory
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
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskStateLogger
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.SyncTaskLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    private val cloudAuthReader: CloudAuthReader,

    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncTaskNotificator: SyncTaskNotificator,

    private val syncTaskLogger: SyncTaskLogger,
    private val taskStateLogger: TaskStateLogger,
    private val executionLogger: ExecutionLogger,

    private val resources: Resources,

    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectStateResetter: SyncObjectStateResetter,

    private val storageToDatabaseListerAssistedFactory: StorageToDatabaseListerAssistedFactory,
) {
    private val executionId: String get() = hashCode().toString()

    private var _currentTaskId: String? = null
    private val currentTaskId get(): String = _currentTaskId!!

    private val currentTask: SyncTask get() = runBlocking {
        syncTaskReader.getSyncTask(currentTaskId)
    }

    private val syncTaskRunningTimeUpdater: SyncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }

    // FIXME: Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker. Как устойчивость к ошибкам?
    suspend fun executeSyncTask(taskId: String) {

        try {
            Log.d(TAG, "")
            Log.d(TAG, "")
            Log.d(tag, "========= executeSyncTask() [${classNameWithHash()}] СТАРТ ========")

            //
            // TODO: Эта подписка мешает SyncTaskWorker завершиться.
            //  А как сделать её автоматически отменяемой?
            //  По идее, надо отменять coroutineScope, в котором она выполняется...
            //
            /*coroutineScope.launch(Dispatchers.IO) {
                syncTaskReader.getSyncTaskAsFlow(taskId).collectLatest { syncTask ->
                    Log.d(TAG, "$syncTask")
                }
            }*/

            _currentTaskId = taskId

            doWork()
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(currentTaskId, ExecutionState.ERROR, t.errorMsg)
            Log.e(TAG, t.errorMsg, t)
            logExecutionError(currentTask, t)
        }
        finally {
//            syncTaskNotificator.hideNotification(taskId, notificationId)

            if (null != _currentTaskId)
                syncTaskRunningTimeUpdater.updateFinishTime(_currentTaskId!!)
            else {
                Log.e(TAG, "================================================================")
                Log.e(TAG, "CANNOT UPDATE TASK FINISH TIME, BECAUSE CURRENT TASK ID IS NULL.")
                Log.e(TAG, "================================================================")
            }
            // Зачем это?
//            currentTask = syncTaskReader.getSyncTask(currentTaskId)
        }

        Log.d(tag, "========= executeSyncTask() [${classNameWithHash()}] ФИНИШ ========")
    }

    /**
     * Важно запускать этот класс в режиме один экземпляр - одна задача (SyncTask).
     * Иначе будут сбрабываться статусы уже выполняющихся задач (!)
     */
    private suspend fun doWork() {

//        showReadingSourceNotification(syncTask.notificationId)

        logExecutionStart(currentTaskId, executionId)


            syncTaskRunningTimeUpdater.updateStartTime(currentTaskId)
            // Вынужденная мера, так как обновляется объект в БД...
//            currentTask = syncTaskReader.getSyncTask(taskId)

            syncTaskStateChanger.changeExecutionState(currentTaskId, ExecutionState.RUNNING)

            // Удалить выполненные инструкции
            deleteProcessedSyncInstructions()

            // Выполнить недоделанные инструкции
            removeDuplicatedUnprocessedSyncInstructions()

            val task1 = currentTask
            prepareBackupDirs()
            val task2 = currentTask
            Log.d(TAG, "$task1 | $task2")


            processUnprocessedSyncInstructions()

            // Выполнить подготовку
            resetTaskBadStates(currentTaskId)
            resetObjectsBadState(currentTaskId)

            markAllObjectsAsNotChecked(currentTaskId)

            // Прочитать источник
            readSource()

            // Прочитать приёмник
            readTarget()

            // Отметить все не найденные объекты как удалённые
            markAllNotCheckedObjectsAsDeleted(currentTaskId)

            deleteOldComparisonStates()

            compareSourceWithTarget()

            generateSyncInstructions()


            processSyncInstructions()


            clearProcessedSyncObjectsWithDeletedState()

            syncTaskStateChanger.changeExecutionState(currentTaskId, ExecutionState.SUCCESS)

            logExecutionFinish()
    }

    private suspend fun prepareBackupDirs() {
        appComponent
            .getBackupDirsPreparerAssistedFactory()
            .create(currentTask)
            .prepareBackupDirs()
    }


    private suspend fun removeDuplicatedUnprocessedSyncInstructions() {
        appComponent
            .getSyncInstructionRepository6()
            .deleteUnprocessedDuplicatedInstructions(currentTaskId)
    }

    private suspend fun clearProcessedSyncObjectsWithDeletedState() {
        appComponent
            .getSyncObjectDeleter()
            .deleteProcessedObjectsWithDeletedState(currentTaskId)
    }

    private suspend fun markAllNotCheckedObjectsAsDeleted(taskId: String) {
        syncObjectStateResetter.markAllNotCheckedObjectsAsDeleted(taskId)
    }

    private suspend fun deleteOldComparisonStates() {
        appComponent
            .getComparisonsDeleter6()
            .deleteAllFor(currentTaskId)
    }

    private suspend fun deleteProcessedSyncInstructions() {
        appComponent
            .getInstructionsDeleter()
            .deleteFinishedInstructionsFor(currentTaskId)
    }

    private suspend fun generateSyncInstructions() {
        appComponent
            .getInstructionsGeneratorAssistedFactory6()
            .create(currentTask, executionId)
            .generate()
    }


    private suspend fun processUnprocessedSyncInstructions() {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory6()
            .create(currentTask, executionId, coroutineScope)
            .processUnprocessedInstructions()
    }


    private suspend fun processSyncInstructions() {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory6()
            .create(currentTask, executionId, coroutineScope)
            .processCurrentInstructions()
    }


    private suspend fun logExecutionStart(taskId: String, executionId: String) {

        executionLogger.log(ExecutionLogItem.createFinishingItem(
            taskId = taskId,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_begins)
        ))

        taskStateLogger.logRunning(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = taskId,
            entryType = ExecutionLogItemType.START
        ))
    }

    private suspend fun logExecutionFinish() {

        executionLogger.log(ExecutionLogItem.createFinishingItem(
            taskId = currentTaskId,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_ends)
        ))

        taskStateLogger.logSuccess(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = currentTaskId,
            entryType = ExecutionLogItemType.FINISH
        ))
    }

    private suspend fun logExecutionError(syncTask: SyncTask, t: Throwable) {

        executionLogger.log(ExecutionLogItem.createErrorItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_error),
            details = t.errorMsg
        ))

        taskStateLogger.logError(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = syncTask.id,
            entryType = ExecutionLogItemType.ERROR,
            errorMsg = null
        ))
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
    private suspend fun readSource(): Result<Boolean> {
        return storageToDatabaseLister
            .listFromPathToDatabase(
                syncSide = SyncSide.SOURCE,
                executionId = executionId,
                cloudAuth = cloudAuthReader.getCloudAuth(currentTask.sourceAuthId!!),
                pathReadingFrom = currentTask.sourcePath!!,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    private suspend fun readTarget() {
        storageToDatabaseLister
            .listFromPathToDatabase(
                syncSide = SyncSide.TARGET,
                executionId = executionId,
                cloudAuth = cloudAuthReader.getCloudAuth(currentTask.targetAuthId!!),
                pathReadingFrom = currentTask.targetPath!!,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    // FIXME: логика
    private val storageToDatabaseLister: StorageToDatabaseLister by lazy {
        storageToDatabaseListerAssistedFactory.create(currentTask)
    }


    private suspend fun compareSourceWithTarget() {
        appComponent
            .getSourceWithTargetComparatorAssistedFactory()
            .create(currentTask, executionId = executionId)
            .compareSourceWithTarget()
    }


    private fun showWritingTargetNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.WRITING_TARGET)
    }

    private fun showReadingSourceNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.READING_SOURCE)
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