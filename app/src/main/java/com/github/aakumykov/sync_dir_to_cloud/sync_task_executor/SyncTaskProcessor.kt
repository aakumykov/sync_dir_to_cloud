package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseLister
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

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

/**
 * Задача класса - выполнять сложную логику шагов выполнения задачи.
 */
class SyncTaskProcessor @AssistedInject constructor(

    @Assisted private val coroutineScope: CoroutineScope,

    private val cloudAuthReader: CloudAuthReader,

    private val syncTaskNotificator: SyncTaskNotificator,

    private val syncTaskStateChanger: SyncTaskStateChanger,

    private val syncObjectStateResetter: SyncObjectStateResetter,

    private val storageToDatabaseListerAssistedFactory: StorageToDatabaseListerAssistedFactory,
) {
    private var _currentTask: SyncTask? = null
    private val currentTask: SyncTask get() = _currentTask!!

    private val currentTaskId get(): String = currentTask.id

    private var _currentExecutionId: String? = null
    private val currentExecutionId get(): String = _currentExecutionId!!



    /**
     * Важно запускать этот класс в режиме один экземпляр - одна задача (SyncTask).
     * Иначе будут сбрабываться статусы уже выполняющихся задач (!)
     */
    suspend fun processSyncTask(syncTask: SyncTask, executionId: String) {
        _currentTask = syncTask
        _currentExecutionId = executionId

        // Проверить каталоги задачи
        checkTaskDirs()

        // Удалить выполненные инструкции
        deleteProcessedSyncInstructions()

        // Выполнить недоделанные инструкции
        removeDuplicatedUnprocessedSyncInstructions()
        prepareBackupDirs() // Для доделки прошлых недоделанных задач.
        processUnprocessedSyncInstructions()

        // Сброс старого состояния задачи и её объектов.
        resetTaskBadStates(currentTaskId)
        resetObjectsBadState(currentTaskId)

        // Чтение хранилищ.
        markAllObjectsAsNotChecked(currentTaskId)
        readSource().getOrThrow()
        readTarget().getOrThrow()
        markAllNotCheckedObjectsAsDeleted(currentTaskId)

        // Сравнение старого состояния объектов с новым.
        deleteOldComparisonStates()
        compareSourceWithTarget()

        // Создание инструкций обработки.
        generateSyncInstructions()

        // Подготавливаю каталоги бекапов нынешних задач.
        prepareBackupDirs()

        processSyncInstructions()

        clearProcessedSyncObjectsWithDeletedState()
    }

    private suspend fun checkTaskDirs() {
        appComponent
            .getTaskDirsCheckerAssistedFactory()
            .create(currentTask, currentExecutionId)
            .checkTaskDirs()
    }

    private suspend fun prepareBackupDirs() {
        appComponent
            .getBackupDirsPreparerAssistedFactory()
            .create(currentTask)
            .prepareBackupDirs()
    }


    private suspend fun removeDuplicatedUnprocessedSyncInstructions() {
        appComponent
            .getSyncInstructionRepository()
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
            .getComparisonsDeleter()
            .deleteAllFor(currentTaskId)
    }

    private suspend fun deleteProcessedSyncInstructions() {
        appComponent
            .getInstructionsDeleter()
            .deleteFinishedInstructionsFor(currentTaskId)
    }

    private suspend fun generateSyncInstructions() {
        appComponent
            .getInstructionsGeneratorAssistedFactory()
            .create(currentTask, currentExecutionId)
            .generate()
    }


    private suspend fun processUnprocessedSyncInstructions() {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory()
            .create(currentTask, currentExecutionId, coroutineScope)
            .processPrevSessionUnprocessedInstructions()
    }


    private suspend fun processSyncInstructions() {
        appComponent
            .getSyncInstructionsProcessorAssistedFactory()
            .create(currentTask, currentExecutionId, coroutineScope)
            .processThisSessionInstructions()
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
                executionId = currentExecutionId,
                cloudAuth = cloudAuthReader.getCloudAuth(currentTask.sourceAuthId!!),
                pathReadingFrom = currentTask.sourcePath!!,
                changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
            )
    }


    private suspend fun readTarget(): Result<Boolean> {
        return storageToDatabaseLister
            .listFromPathToDatabase(
                syncSide = SyncSide.TARGET,
                executionId = currentExecutionId,
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
            .create(currentTask, currentExecutionId)
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
        val TAG: String = SyncTaskProcessor::class.java.simpleName
    }
}