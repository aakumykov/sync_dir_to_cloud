package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.DeleteInstructionExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class DeleteInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val deleteInstructionExecutorAssistedFactory: DeleteInstructionExecutorAssistedFactory,
    private val basicInstructionsProcessorAssistedFactory: BasicInstructionsProcessorAssistedFactory,
) {
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(list.filter { it.isDeletion })
    }

    private suspend fun processReal(deletionInstructionList: Iterable<SyncInstruction>) {
        processDeletion(deletionInstructionList.filter { it.isFile }, R.string.LOG_ITEM_deleting_file)
        processDeletion(deletionInstructionList.filter { it.isDir }, R.string.LOG_ITEM_deleting_dir)
    }

    private suspend fun processDeletion(list: Iterable<SyncInstruction>, @StringRes operationName: Int) {
        list.forEach { instruction ->
            basicInstructionsProcessor.process(
                parentScope = parentScope,
                operationName = operationName,
                firstItem = instruction.relativePath,
                secondItem = null,
            ) {
                deleteInstructionExecutor.execute(instruction)
            }
        }
    }

    private val deleteInstructionExecutor by lazy {
        deleteInstructionExecutorAssistedFactory.create(syncTask, executionId, parentScope)
    }

    private val basicInstructionsProcessor by lazy {
        basicInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
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