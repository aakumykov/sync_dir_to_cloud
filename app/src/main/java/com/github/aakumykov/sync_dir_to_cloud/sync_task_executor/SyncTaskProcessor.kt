package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseLister
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.BackupDirsPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_80_comparison.ComparisonsDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_80_comparison.SourceWithTargetComparatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.CoroutineSyncInstructionExecutor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.CoroutineSyncInstructionsProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.SyncInstructionDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.SyncInstructionsProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.generator.InstructionsGeneratorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.task_dirs_checker.TaskDirsFixerAssistedFactory
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
 * Иначе будут сбрасываться статусы уже выполняющихся задач (!) FIXME: это что за прикол?
 */
class SyncTaskProcessor @AssistedInject constructor(

    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,

    private val syncInstructionsProcessorAssistedFactory: SyncInstructionsProcessorAssistedFactory,
    private val coroutineSyncInstructionsProcessorAssistedFactory: CoroutineSyncInstructionsProcessorAssistedFactory,

    private val sourceWithTargetComparatorAssistedFactory: SourceWithTargetComparatorAssistedFactory,
    private val instructionsGeneratorAssistedFactory: InstructionsGeneratorAssistedFactory,
    private val backupDirsPreparerAssistedFactory: BackupDirsPreparerAssistedFactory,
    private val taskDirsFixerAssistedFactory: TaskDirsFixerAssistedFactory,
    private val storageToDatabaseListerAssistedFactory: StorageToDatabaseListerAssistedFactory,

    private val cloudAuthReader: CloudAuthReader,
    private val syncTaskNotificator: SyncTaskNotificator,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncObjectStateResetter: SyncObjectStateResetter,
    private val syncInstructionDeleter: SyncInstructionDeleter,
    private val comparisonsDeleter: ComparisonsDeleter,
    private val syncObjectDeleter:SyncObjectDBDeleter,
    private val syncInstructionRepository:SyncInstructionRepository,
) {
    private val taskId: String get() = syncTask.id

    private val mCoroutineSyncInstructionExecutor: CoroutineSyncInstructionExecutor by lazy {
        coroutineSyncInstructionsProcessorAssistedFactory.create(taskId, executionId, scope)
    }

    private val fileInstructionsProcessor by lazy {
        syncInstructionsProcessorAssistedFactory.create(syncTask, executionId, scope)
    }

    // FIXME: логика
    private val storageToDatabaseLister: StorageToDatabaseLister by lazy {
        storageToDatabaseListerAssistedFactory.create(syncTask)
    }

    private val sourceWithTargetComparator by lazy {
        sourceWithTargetComparatorAssistedFactory.create(syncTask, executionId)
    }

    private val instructionsGenerator by lazy {
        instructionsGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val backupsDirPreparer by lazy {
        backupDirsPreparerAssistedFactory.create(syncTask)
    }

    private val taskDirsFixer by lazy {
        taskDirsFixerAssistedFactory.create(syncTask, executionId)
    }



    suspend fun processSyncTask() {

        // Проверить каталоги задачи
        checkTaskDirs()

        // Удалить выполненные инструкции
        deleteProcessedSyncInstructions()

        // Выполнить недоделанные инструкции
        removeDuplicatedUnprocessedSyncInstructions()
        prepareBackupDirs(R.string.preparing_backup_dirs_for_previous_unfinished_tasks) // Для доделки прошлых недоделанных задач.
        processUnprocessedFileInstructions()

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
        generateFileInstructions()

        // Подготавливаю каталоги бекапов нынешних задач.
        prepareBackupDirs(R.string.preparing_backup_dirs_for_current_task)

        processNewFileInstructions()

        clearProcessedSyncObjectsWithDeletedState()
    }


    private suspend fun checkTaskDirs() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.checking_task_dirs),
            instructionBlock = {
                taskDirsFixer.fixTaskDirs()
            }
        )
    }


    private suspend fun prepareBackupDirs(@StringRes logMessageId: Int) {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(logMessageId),
            instructionBlock = {
                backupsDirPreparer.prepareBackupDirs()
            }
        )
    }


    private suspend fun removeDuplicatedUnprocessedSyncInstructions() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.removing_duplicate_file_instructions),
            instructionBlock = {
                syncInstructionRepository.deleteUnprocessedDuplicatedInstructions(taskId)
            }
        )
    }


    private suspend fun clearProcessedSyncObjectsWithDeletedState() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.clearing_processed_sync_objects_with_deleted_state),
            instructionBlock = {
                syncObjectDeleter.deleteProcessedObjectsWithDeletedState(taskId)
            }
        )
    }


    private suspend fun markAllNotCheckedObjectsAsDeleted() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.marking_all_not_checked_objects_as_deleted),
            instructionBlock = {
                syncObjectStateResetter.markAllNotCheckedObjectsAsDeleted(taskId)
            }
        )
    }


    private suspend fun deleteOldComparisonStates() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.deleting_old_comparison_results),
            instructionBlock = {
                comparisonsDeleter.deleteAllFor(taskId)
            }
        )
    }


    private suspend fun deleteProcessedSyncInstructions() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.removing_processed_file_instructions),
            instructionBlock = {
                syncInstructionDeleter.deleteFinishedInstructionsFor(taskId)
            }
        )
    }


    private suspend fun generateFileInstructions() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.generating_file_instructions),
            instructionBlock = {
                instructionsGenerator.generateFileInstructions()
            }
        )
    }


    private suspend fun processUnprocessedFileInstructions() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.processing_unprocessed_file_instructions),
            instructionBlock = {
                fileInstructionsProcessor.processPrevSessionUnprocessedInstructions()
            }
        )
    }


    private suspend fun processNewFileInstructions() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.processing_file_instructions),
            instructionBlock = {
                fileInstructionsProcessor.processThisSessionInstructions()
            }
        )
    }



    private suspend fun resetTaskBadStates() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.resetting_task_bad_states),
            instructionBlock = {
                syncTaskStateChanger.resetSourceReadingBadState(taskId)
            }
        )
    }

    private suspend fun resetObjectsBadState() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.resetting_objects_bad_states),
            instructionBlock = {
                syncObjectStateResetter.resetTargetReadingBadState(taskId)
                syncObjectStateResetter.resetBackupBadState(taskId)
                syncObjectStateResetter.resetBackupBadState(taskId)
                syncObjectStateResetter.resetDeletionBadState(taskId)
                syncObjectStateResetter.resetSyncBadState(taskId)
            }
        )
    }


    private suspend fun markAllObjectsAsNotChecked() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.marking_all_objects_as_not_checked),
            instructionBlock = {
                syncObjectStateResetter.markAllObjectsAsNotChecked(taskId)
            }
        )
    }


    /**
     * @return Флаг успешности чтения источника.
     */
    private suspend fun readSource() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.reading_source),
            instructionBlock = {
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
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.reading_target),
            instructionBlock = {
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


    private suspend fun compareSourceWithTarget() {
        mCoroutineSyncInstructionExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.comparing_source_with_target),
            instructionBlock = {
                sourceWithTargetComparator.compareSourceWithTarget()
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