package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseLister
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.CoroutineSyncInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.CoroutineSyncInstructionsProcessorAssistedFactory
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
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
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
 *
 * Важно запускать этот класс в режиме один экземпляр - одна задача (SyncTask).
 * Иначе будут сбрасываться статусы уже выполняющихся задач (!)
 */
class SyncTaskProcessor @AssistedInject constructor(

    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,

    private val coroutineSyncInstructionsProcessorAssistedFactory: CoroutineSyncInstructionsProcessorAssistedFactory,

    private val cloudAuthReader: CloudAuthReader,

    private val syncTaskNotificator: SyncTaskNotificator,

    private val syncTaskStateChanger: SyncTaskStateChanger,

    private val syncObjectStateResetter: SyncObjectStateResetter,

    private val storageToDatabaseListerAssistedFactory: StorageToDatabaseListerAssistedFactory,
) {
    private val taskId: String get() = syncTask.id

    private val coroutineSyncInstructionsProcessor: CoroutineSyncInstructionsProcessor by lazy {
        coroutineSyncInstructionsProcessorAssistedFactory.create(taskId, executionId, scope)
    }


    suspend fun processSyncTask() {

        // Проверить каталоги задачи
        checkTaskDirs()

        // Удалить выполненные инструкции
        deleteProcessedSyncInstructions()

        // Выполнить недоделанные инструкции
        removeDuplicatedUnprocessedSyncInstructions()
        prepareBackupDirs(R.string.preparing_backup_dirs_for_previous_unfinished_tasks) // Для доделки прошлых недоделанных задач.
        processUnprocessedSyncInstructions()

        // Сброс старого состояния задачи и её объектов.
        resetTaskBadStates()
        resetObjectsBadState()

        // Чтение хранилищ.
        markAllObjectsAsNotChecked()
        readSource()
        readTarget()
        markAllNotCheckedObjectsAsDeleted()

        // Сравнение старого состояния объектов с новым.
        deleteOldComparisonStates()
        compareSourceWithTarget()

        // Создание инструкций обработки.
        generateSyncInstructions()

        // Подготавливаю каталоги бекапов нынешних задач.
        prepareBackupDirs(R.string.preparing_backup_dirs_for_current_task)

        processSyncInstructions()

        clearProcessedSyncObjectsWithDeletedState()
    }

    private suspend fun checkTaskDirs() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.checking_task_dirs),
            executionBlock = {
                appComponent
                    .getTaskDirsCheckerAssistedFactory()
                    .create(syncTask, executionId)
                    .checkTaskDirs()
            }
        )
    }

    private suspend fun prepareBackupDirs(@StringRes logMessageId: Int) {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(logMessageId),
            executionBlock = {
                appComponent
                    .getBackupDirsPreparerAssistedFactory()
                    .create(syncTask)
                    .prepareBackupDirs()
            }
        )
    }


    private suspend fun removeDuplicatedUnprocessedSyncInstructions() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.removing_duplicate_sync_instructions),
            executionBlock = {
                appComponent
                    .getSyncInstructionRepository()
                    .deleteUnprocessedDuplicatedInstructions(taskId)
            }
        )
    }

    private suspend fun clearProcessedSyncObjectsWithDeletedState() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.clearing_processed_sync_objects_with_deleted_state),
            executionBlock = {
                appComponent
                    .getSyncObjectDeleter()
                    .deleteProcessedObjectsWithDeletedState(taskId)
            }
        )
    }

    private suspend fun markAllNotCheckedObjectsAsDeleted() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.marking_all_not_checked_objects_as_deleted),
            executionBlock = {
                syncObjectStateResetter.markAllNotCheckedObjectsAsDeleted(taskId)
            }
        )
    }

    private suspend fun deleteOldComparisonStates() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.deleting_old_comparison_results),
            executionBlock = {
                appComponent
                    .getComparisonsDeleter()
                    .deleteAllFor(taskId)
            }
        )
    }

    private suspend fun deleteProcessedSyncInstructions() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.removing_processed_sync_instructions),
            executionBlock = {
                appComponent
                    .getInstructionsDeleter()
                    .deleteFinishedInstructionsFor(taskId)
            }
        )
    }

    private suspend fun generateSyncInstructions() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.generating_sync_instructions),
            executionBlock = {
                appComponent
                    .getInstructionsGeneratorAssistedFactory()
                    .create(syncTask, executionId)
                    .generate()
            }
        )
    }


    private suspend fun processUnprocessedSyncInstructions() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.processing_unprocessed_sync_instructions),
            executionBlock = {
                appComponent
                    .getSyncInstructionsProcessorAssistedFactory()
                    .create(syncTask, executionId, scope)
                    .processPrevSessionUnprocessedInstructions()
            }
        )
    }


    private suspend fun processSyncInstructions() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.processing_sync_instructions),
            executionBlock = {
                appComponent
                    .getSyncInstructionsProcessorAssistedFactory()
                    .create(syncTask, executionId, scope)
                    .processThisSessionInstructions()
            }
        )
    }



    private suspend fun resetTaskBadStates() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.resetting_task_bad_states),
            executionBlock = {
                syncTaskStateChanger.resetSourceReadingBadState(taskId)
            }
        )
    }

    private suspend fun resetObjectsBadState() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.resetting_objects_bad_states),
            executionBlock = {
                syncObjectStateResetter.resetTargetReadingBadState(taskId)
                syncObjectStateResetter.resetBackupBadState(taskId)
                syncObjectStateResetter.resetBackupBadState(taskId)
                syncObjectStateResetter.resetDeletionBadState(taskId)
                syncObjectStateResetter.resetSyncBadState(taskId)
            }
        )
    }


    private suspend fun markAllObjectsAsNotChecked() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.marking_all_objects_as_not_checked),
            executionBlock = {
                syncObjectStateResetter.markAllObjectsAsNotChecked(taskId)
            }
        )
    }


    /**
     * @return Флаг успешности чтения источника.
     */
    private suspend fun readSource() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.reading_source),
            executionBlock = {
                storageToDatabaseLister
                    .listFromPathToDatabase(
                        syncSide = SyncSide.SOURCE,
                        executionId = executionId,
                        cloudAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId!!),
                        pathReadingFrom = syncTask.sourcePath!!,
                        changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
                    )
            }
        )
    }


    private suspend fun readTarget() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.reading_target),
            executionBlock = {
                storageToDatabaseLister
                    .listFromPathToDatabase(
                        syncSide = SyncSide.TARGET,
                        executionId = executionId,
                        cloudAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!),
                        pathReadingFrom = syncTask.targetPath!!,
                        changesDetectionStrategy = ChangesDetectionStrategy.SIZE_AND_MODIFICATION_TIME
                    )
            }
        )
    }


    // FIXME: логика
    private val storageToDatabaseLister: StorageToDatabaseLister by lazy {
        storageToDatabaseListerAssistedFactory.create(syncTask)
    }


    private suspend fun compareSourceWithTarget() {
        coroutineSyncInstructionsProcessor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.comparing_source_with_target),
            executionBlock = {
                appComponent
                    .getSourceWithTargetComparatorAssistedFactory()
                    .create(syncTask, executionId)
                    .compareSourceWithTarget()
            }
        )
    }


    private suspend fun showWritingTargetNotification(syncTask: SyncTask) {
        syncTaskNotificator.showNotification(syncTask.id, syncTask.notificationId, SyncTask.State.WRITING_TARGET)
    }

    private suspend fun showReadingSourceNotification(syncTask: SyncTask) {
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


@AssistedFactory
interface SyncTaskProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        coroutineScope: CoroutineScope
    ): SyncTaskProcessor
}