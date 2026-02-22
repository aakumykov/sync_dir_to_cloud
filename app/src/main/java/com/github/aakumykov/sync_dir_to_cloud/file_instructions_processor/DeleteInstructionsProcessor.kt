package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.DeleteInstructionExecutor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.DeleteInstructionExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.DatabaseFileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll

class DeleteInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    fileOperationLogger: DatabaseFileOperationLogger,
    syncInstructionUpdater: SyncInstructionUpdater,
    syncObjectDBReader: SyncObjectDBReader,
    private val deleteInstructionExecutorAssistedFactory: DeleteInstructionExecutorAssistedFactory,
)
    : CommonFileInstructionsProcessor(
        fileOperationLogger, syncInstructionUpdater, syncObjectDBReader)
{
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(list.filter { it.isDeletion })
    }

    private suspend fun processReal(deletionInstructionList: Iterable<SyncInstruction>) {
        processDeletion(deletionInstructionList.filter { it.isFile }, R.string.LOG_ITEM_deleting_file)
        processDeletion(deletionInstructionList.filter { it.isDir }, R.string.LOG_ITEM_deleting_dir)
    }

    private suspend fun processDeletion(list: Iterable<SyncInstruction>, @StringRes operationName: Int) {
        list.map { instruction ->

            val jobId = newRandomId
            val logItemId = newRandomId

            val logBaseInfo = DatabaseFileOperationLogger.LogBaseInfo(
                taskId = syncTask.id,
                executionId = executionId,
                logItemId = logItemId,
                operationName = operationName,
                firstItem = null,
                secondItem = null
            )

            runInCoroutineExtended(
                scope = parentScope,
                onStart = { logStarted(logBaseInfo, jobId = jobId) },
                onFinish = { logFinished(logBaseInfo) },
                onCancel = { logCancelled(logBaseInfo, it) },
                onError = { logError(logBaseInfo, it) },
            ) {
                deleteInstructionExecutor.execute(instruction)
            }.also { a ->
                markInstructionAsProcessed(instruction)
            }

        }.joinAll()
    }

    private val deleteInstructionExecutor: DeleteInstructionExecutor by lazy {
        deleteInstructionExecutorAssistedFactory.create(syncTask, parentScope)
    }
}

@AssistedFactory
interface DeleteInstructionsProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        parentScope: CoroutineScope
    ): DeleteInstructionsProcessor
}