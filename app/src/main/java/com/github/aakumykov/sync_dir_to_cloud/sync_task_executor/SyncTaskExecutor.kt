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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single

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

    private var currentTask: SyncTask? = null

    private val syncTaskRunningTimeUpdater: SyncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }

    // FIXME: Не ловлю здесь исключения, чтобы их увидел SyncTaskWorker. Как устойчивость к ошибкам?
    suspend fun executeSyncTask(taskId: String) {

        Log.d(TAG, "")
        Log.d(TAG, "")
        Log.d(tag, "========= executeSyncTask() [${classNameWithHash()}] СТАРТ ========")

        syncTaskReader.getSyncTask(taskId).also {  syncTask ->
            currentTask = syncTask
            doWork(syncTask, syncTaskReader.getSyncTaskAsFlow(taskId))
        }

        Log.d(tag, "========= executeSyncTask() [${classNameWithHash()}] ФИНИШ ========")
    }

    /**
     * Важно запускать этот класс в режиме один экземпляр - одна задача (SyncTask).
     * Иначе будут сбрабываться статусы уже выполняющихся задач (!)
     */
    private suspend fun doWork(syncTask: SyncTask, syncTaskFlow: Flow<SyncTask>) {

        val taskId = syncTask.id
        val notificationId = syncTask.notificationId

//        showReadingSourceNotification(syncTask)

        logExecutionStart(syncTask, executionId)

        try {
            syncTaskRunningTimeUpdater.updateStartTime(taskId)
            // Вынужденная мера, так как обновляется объект в БД...
//            currentTask = syncTaskReader.getSyncTask(taskId)

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            // Удалить выполненные инструкции
            deleteProcessedSyncInstructions(syncTaskFlow)

            // Выполнить недоделанные инструкции
            removeDuplicatedUnprocessedSyncInstructions(syncTaskFlow)

            prepareBackupDirs(syncTaskFlow)

            processUnprocessedSyncInstructions(syncTaskFlow)

            // Выполнить подготовку
            resetTaskBadStates(taskId)
            resetObjectsBadState(taskId)

            markAllObjectsAsNotChecked(taskId)

            // Прочитать источник
            readSource(syncTaskFlow)

            // Прочитать приёмник
            readTarget(syncTaskFlow)

            // Отметить все не найденные объекты как удалённые
            markAllNotCheckedObjectsAsDeleted(taskId)

            deleteOldComparisonStates(syncTaskFlow)

            compareSourceWithTarget(syncTaskFlow)

            generateSyncInstructions(syncTaskFlow)

            // Подготовка каталогов бекапа после
            // чтения источника и приёмника,
            // создания инструкций,
            // но перед выполнением этих инструкций.
//            prepareBackupDirs(currentTask!!)


            // После подготовки нужно перечитать SyncTask.
            // Вынужденная мера, так как обновляется объект в БД...
//            currentTask = syncTaskReader.getSyncTask(taskId)


            processSyncInstructions(syncTaskFlow)
            clearProcessedSyncObjectsWithDeletedState(syncTaskFlow)



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

            logExecutionFinish(syncTaskFlow)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, t.errorMsg)
            Log.e(TAG, t.errorMsg, t)
            logExecutionError(syncTask, t)
        }
        finally {
//            syncTaskNotificator.hideNotification(taskId, notificationId)
            syncTaskRunningTimeUpdater.updateFinishTime(taskId)
            currentTask = syncTaskReader.getSyncTask(taskId)
        }
    }

    private suspend fun prepareBackupDirs(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getBackupDirsPreparerAssistedFactory()
            .create(syncTaskFlow.single())
            .prepareBackupDirs()
    }


    private suspend fun removeDuplicatedUnprocessedSyncInstructions(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getSyncInstructionRepository6()
            .deleteUnprocessedDuplicatedInstructions(syncTaskFlow.single().id)
    }

    private suspend fun clearProcessedSyncObjectsWithDeletedState(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getSyncObjectDeleter()
            .deleteProcessedObjectsWithDeletedState(syncTaskFlow.single().id)
    }

    private suspend fun markAllNotCheckedObjectsAsDeleted(taskId: String) {
        syncObjectStateResetter.markAllNotCheckedObjectsAsDeleted(taskId)
    }

    private suspend fun deleteOldComparisonStates(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getComparisonsDeleter6()
            .deleteAllFor(syncTaskFlow.single().id)
    }

    private suspend fun deleteProcessedSyncInstructions(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getInstructionsDeleter()
            .deleteFinishedInstructionsFor(syncTaskFlow.single().id)
    }

    private suspend fun generateSyncInstructions(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getInstructionsGeneratorAssistedFactory6()
            .create(syncTaskFlow.single(), executionId)
            .generate()
    }


    private suspend fun processUnprocessedSyncInstructions(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory6()
            .create(syncTaskFlow.single(), executionId, coroutineScope)
            .processUnprocessedInstructions()
    }


    private suspend fun processSyncInstructions(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory6()
            .create(syncTaskFlow.single(), executionId, coroutineScope)
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

    private suspend fun logExecutionFinish(syncTaskFlow: Flow<SyncTask>) {

        executionLogger.log(ExecutionLogItem.createFinishingItem(
            taskId = syncTaskFlow.single().id,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_ends)
        ))

        taskStateLogger.logSuccess(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = syncTaskFlow.single().id,
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
    private suspend fun readSource(syncTaskFlow: Flow<SyncTask>): Result<Boolean> {
        val syncTask = syncTaskFlow.single()
        return storageToDatabaseLister
            .listFromPathToDatabase(
                syncSide = SyncSide.SOURCE,
                executionId = executionId,
                cloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId!!),
                pathReadingFrom = syncTask.sourcePath!!,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    private suspend fun readTarget(syncTaskFlow: Flow<SyncTask>) {
        val syncTask = syncTaskFlow.single()
        storageToDatabaseLister
            .listFromPathToDatabase(
                syncSide = SyncSide.TARGET,
                executionId = executionId,
                cloudAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!),
                pathReadingFrom = syncTask.targetPath!!,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    // FIXME: логика
    private val storageToDatabaseLister: StorageToDatabaseLister by lazy {
        storageToDatabaseListerAssistedFactory.create(currentTask!!)
    }


    private suspend fun compareSourceWithTarget(syncTaskFlow: Flow<SyncTask>) {
        appComponent
            .getSourceWithTargetComparatorAssistedFactory()
            .create(syncTask = syncTaskFlow.single(), executionId = executionId)
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