package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class FileInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val collisionResolverInstructionsProcessorAssistedFactory: CollisionResolverInstructionsProcessorAssistedFactory,
    private val backupInstructionsProcessorAssistedFactory: BackupInstructionsProcessorAssistedFactory,
    private val copyInstructionsProcessorAssistedFactory: CopyInstructionsProcessorAssistedFactory,
    private val deleteInstructionsProcessorAssistedFactory: DeleteInstructionsProcessorAssistedFactory,
){
    suspend fun process(parentScope: CoroutineScope, list: List<SyncInstruction>) {
        list.forEach { instruction ->
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
        }
    }

    private val collisionResolverInstructionsProcessor by lazy {
        collisionResolverInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
    }

    private val backupInstructionsProcessor by lazy {
        backupInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
    }

    private val deleteInstructionsProcessor by lazy {
        deleteInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
    }

    private val copyInstructionsProcessor by lazy {
        copyInstructionsProcessorAssistedFactory.create(syncTask, executionId)
    }
}

@AssistedFactory
interface FileInstructionsProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String
    ): FileInstructionsProcessor
}