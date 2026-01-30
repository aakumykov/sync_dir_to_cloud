package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.extensions.notProcessed
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class FileInstructionsProcessor2 @AssistedInject constructor(
    @Assisted private val parentScope: CoroutineScope,
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncInstructionRepository: SyncInstructionRepository,
    private val collisionResolverInstructionsProcessorAssistedFactory: CollisionResolverInstructionsProcessorAssistedFactory,
    private val backupInstructionsProcessorAssistedFactory: BackupInstructionsProcessorAssistedFactory,
    private val copyInstructionsProcessorAssistedFactory: CopyInstructionsProcessorAssistedFactory,
    private val deleteInstructionsProcessorAssistedFactory: DeleteInstructionsProcessorAssistedFactory,
){
    suspend fun processFileInstructions(selectUnprocessed: Boolean) {
        /*list.forEach { instruction ->
            when(instruction.operation) {
                SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> copyInstructionsProcessor.process(parentScope, instruction)
                SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> copyInstructionsProcessor.process(parentScope, instruction)

                SyncOperation.RESOLVE_COLLISION -> collisionResolverInstructionsProcessor.process(instruction)

                SyncOperation.BACKUP_IN_SOURCE -> backupInstructionsProcessor.process(instruction)
                SyncOperation.BACKUP_IN_TARGET -> backupInstructionsProcessor.process(instruction)

                SyncOperation.DELETE_IN_SOURCE -> deleteInstructionsProcessor.process(instruction)
                SyncOperation.DELETE_IN_TARGET -> deleteInstructionsProcessor.process(instruction)

                SyncOperation.DO_NOTHING_IN_SOURCE -> {}
                SyncOperation.DO_NOTHING_IN_TARGET -> {}
            }
        }*/

        backupInstructionsProcessor.process(list(selectUnprocessed))
    }

    private suspend fun list(selectUnprocessed: Boolean): Iterable<SyncInstruction> {
        return if (selectUnprocessed) getNonProcessedInstructionsForTask()
        else getNonProcessedSyncInstructionsForTaskAndExecution()
    }

    private suspend fun getNonProcessedInstructionsForTask(): Iterable<SyncInstruction> {
        return syncInstructionRepository
            .getAllWithoutExecutionId(syncTask.id)
            .filter { it.notProcessed }
    }

    private suspend fun getNonProcessedSyncInstructionsForTaskAndExecution(): Iterable<SyncInstruction> {
        return syncInstructionRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.notProcessed }
    }

    private val collisionResolverInstructionsProcessor by lazy {
        collisionResolverInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
    }

    private val backupInstructionsProcessor by lazy {
        backupInstructionsProcessorAssistedFactory.create(syncTask, executionId, parentScope)
    }

    private val deleteInstructionsProcessor by lazy {
        deleteInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
    }

    private val copyInstructionsProcessor by lazy {
        copyInstructionsProcessorAssistedFactory.create(syncTask, executionId)
    }
}

@AssistedFactory
interface FileInstructionsProcessor2AssistedFactory {
    fun create(
        scope: CoroutineScope,
        syncTask: SyncTask,
        executionId: String
    ): FileInstructionsProcessor2
}