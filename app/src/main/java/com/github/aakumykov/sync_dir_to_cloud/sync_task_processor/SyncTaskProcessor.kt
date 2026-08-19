package com.github.aakumykov.sync_dir_to_cloud.sync_task_processor

import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseLister
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list.StorageToDatabaseListerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.BackupDirsPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_80_comparison.ComparisonsDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_80_comparison.SourceWithTargetComparatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_85_generator.InstructionsGeneratorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.OneStageOfTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.OneStageOfTaskExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.SyncInstructionDeleter
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettings
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.CommonFileInstructionsProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.task_dirs_checker.TaskDirsFixerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

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
    @Assisted private val notificator: SyncTaskNotificator,

    private val sourceWithTargetComparatorAssistedFactory: SourceWithTargetComparatorAssistedFactory,
    private val instructionsGeneratorAssistedFactory: InstructionsGeneratorAssistedFactory,
    private val backupDirsPreparerAssistedFactory: BackupDirsPreparerAssistedFactory,
    private val taskDirsFixerAssistedFactory: TaskDirsFixerAssistedFactory,
    private val storageToDatabaseListerAssistedFactory: StorageToDatabaseListerAssistedFactory,

    private val commonFileInstructionsProcessorAssistedFactory: CommonFileInstructionsProcessorAssistedFactory,

    private val oneStageOfTaskExecutorAssistedFactory: OneStageOfTaskExecutorAssistedFactory,

    private val cloudAuthReader: CloudAuthReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val syncObjectStateResetter: SyncObjectStateResetter,
    private val syncInstructionDeleter: SyncInstructionDeleter,
    private val comparisonsDeleter: ComparisonsDeleter,
    private val syncObjectDeleter:SyncObjectDBDeleter,
    private val syncInstructionRepository:SyncInstructionRepository,

    private val appSettings: AppSettings,
) {
    suspend fun processSyncTask() {

        // Проверить каталоги задачи
        checkTaskDirs()

        // Удалить выполненные инструкции
        deleteProcessedSyncInstructions()

        // Выполнить недоделанные инструкции
        removeDuplicatedUnprocessedSyncInstructions()
        prepareBackupDirs(R.string.preparing_backup_dirs_for_previous_unfinished_tasks) // Для доделки прошлых недоделанных задач.
        processFileInstructions(true)

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

        processFileInstructions(false)

        clearProcessedSyncObjectsWithDeletedState()
    }


    private suspend fun checkTaskDirs() {
        Log.d(TAG, "checkTaskDirs()")

        updateNotification(messageId = R.string.checking_task_dirs)

        if (appSettings.dryRun) {
            Log.d(TAG, "Имитация работы, операции с файлами выполнены не будут.")
            return
        }

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.checking_task_dirs),
            codeBlock = {
                taskDirsFixer.fixTaskDirs()
            }
        )
    }


    private suspend fun prepareBackupDirs(@StringRes logMessageId: Int) {
        Log.d(TAG, "prepareBackupDirs()")

        if (appSettings.dryRun) {
            Log.d(TAG, "Имитация работы, операции с файлами выполнены не будут.")
            return
        }

        updateNotification(logMessageId)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(logMessageId),
            codeBlock = {
                backupsDirPreparer.prepareBackupDirs()
            }
        )
    }


    private suspend fun removeDuplicatedUnprocessedSyncInstructions() {
        updateNotification(R.string.removing_duplicate_file_instructions)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.removing_duplicate_file_instructions),
            codeBlock = {
                syncInstructionRepository.deleteUnprocessedDuplicatedInstructions(taskId)
            }
        )
    }


    private suspend fun clearProcessedSyncObjectsWithDeletedState() {
        updateNotification(R.string.clearing_processed_sync_objects_with_deleted_state)

        oneStageOfTaskExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.clearing_processed_sync_objects_with_deleted_state),
            codeBlock = {
                syncObjectDeleter.deleteProcessedObjectsWithDeletedState(taskId)
            }
        )
    }


    private suspend fun markAllNotCheckedObjectsAsDeleted() {
        updateNotification(R.string.marking_all_not_checked_objects_as_deleted)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.marking_all_not_checked_objects_as_deleted),
            codeBlock = {
                syncObjectStateResetter.markAllNotCheckedObjectsAsDeleted(taskId)
            }
        )
    }


    private suspend fun deleteOldComparisonStates() {
        updateNotification(R.string.deleting_old_comparison_results)

        oneStageOfTaskExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.deleting_old_comparison_results),
            codeBlock = {
                comparisonsDeleter.deleteAllFor(taskId)
            }
        )
    }


    private suspend fun deleteProcessedSyncInstructions() {
        updateNotification(R.string.removing_processed_file_instructions)

        oneStageOfTaskExecutor.process(
            isCritical = false,
            logMessage = TextMessage(R.string.removing_processed_file_instructions),
            codeBlock = {
                syncInstructionDeleter.deleteFinishedInstructionsFor(taskId)
            }
        )
    }


    private suspend fun generateFileInstructions() {
        updateNotification(R.string.generating_file_instructions)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.generating_file_instructions),
            codeBlock = {
                instructionsGenerator.generateFileInstructions()
            }
        )
    }


    private suspend fun processFileInstructions(unprocessed: Boolean) {
        Log.d(TAG, "processFileInstructions(unprocessed:$unprocessed)")

        if (appSettings.dryRun) {
            Log.d(TAG, "Имитация работы, операции с файлами выполнены не будут.")
            return
        }

        val logMessageId: Int = if (unprocessed) R.string.processing_unprocessed_file_instructions
                                else R.string.processing_file_instructions

        val logMessage = TextMessage(logMessageId)

        updateNotification(logMessageId)

        oneStageOfTaskExecutor.process(
            isCritical = !unprocessed,
            logMessage = logMessage,
            codeBlock = {
                commonFileInstructionsProcessor.processFileInstructions(unprocessed)
            }
        )
    }



    private suspend fun resetTaskBadStates() {

        updateNotification(R.string.resetting_task_bad_states)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.resetting_task_bad_states),
            codeBlock = {
                syncTaskStateChanger.resetSourceReadingBadState(taskId)
            }
        )
    }

    private suspend fun resetObjectsBadState() {
        updateNotification(R.string.resetting_objects_bad_states)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.resetting_objects_bad_states),
            codeBlock = {
                syncObjectStateResetter.resetTargetReadingBadState(taskId)
                syncObjectStateResetter.resetBackupBadState(taskId)
                syncObjectStateResetter.resetBackupBadState(taskId)
                syncObjectStateResetter.resetDeletionBadState(taskId)
                syncObjectStateResetter.resetSyncBadState(taskId)
            }
        )
    }


    private suspend fun markAllObjectsAsNotChecked() {
        updateNotification(R.string.marking_all_objects_as_not_checked)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.marking_all_objects_as_not_checked),
            codeBlock = {
                syncObjectStateResetter.markAllObjectsAsNotChecked(taskId)
            }
        )
    }


    /**
     * @return Флаг успешности чтения источника.
     */
    private suspend fun readSource() {
        updateNotification(R.string.reading_source)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.reading_source),
            codeBlock = {
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
        updateNotification(R.string.reading_target)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.reading_target),
            codeBlock = {
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
        updateNotification(R.string.comparing_source_with_target)

        oneStageOfTaskExecutor.process(
            isCritical = true,
            logMessage = TextMessage(R.string.comparing_source_with_target),
            codeBlock = {
                sourceWithTargetComparator.compareSourceWithTarget()
            }
        )
    }


    private suspend fun updateNotification(@StringRes messageId: Int) {
        notificator.updateProgressNotification(messageId)
        delay(1000)
    }

    /*private suspend fun updateNotification(message: String) {
        notificator.updateProgressNotification(message)
        delay(1000)
    }*/


    suspend fun stopExecutingTask(taskId: String) {
        // TODO: по-настоящему прерывать работу CloudWriterGetter-а
        MyLogger.d(tag, "stopExecutingTask(), [${hashCode()}]")
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.NEVER)
    }


    private val taskId: String get() = syncTask.id

    private val oneStageOfTaskExecutor: OneStageOfTaskExecutor by lazy {
        oneStageOfTaskExecutorAssistedFactory.create(taskId, executionId, scope)
    }

    private val commonFileInstructionsProcessor by lazy {
        commonFileInstructionsProcessorAssistedFactory.create(scope, syncTask, executionId)
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


    companion object {
        val TAG: String = SyncTaskProcessor::class.java.simpleName
    }
}


@AssistedFactory
interface SyncTaskProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        coroutineScope: CoroutineScope,
        notificator: SyncTaskNotificator,
    ): SyncTaskProcessor
}