package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.notProcessed
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

/**
 * Исполняет инструкции в определённом порядке.
 */
class FileInstructionsProcessor2 @AssistedInject constructor(
    @Assisted private val parentScope: CoroutineScope,
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,

    private val syncInstructionRepository: SyncInstructionRepository,

    private val backupInstructionsProcessorAssistedFactory: BackupInstructionsProcessorAssistedFactory,
    private val collisionResolverInstructionsProcessorAssistedFactory: CollisionResolverInstructionsProcessorAssistedFactory,
    private val dirCreationInstructionsProcessorAssistedFactory: DirCreationInstructionsProcessorAssistedFactory,
    private val fileCopyInstructionsProcessorAssistedFactory: FileCopyInstructionsProcessorAssistedFactory,
    private val deleteInstructionsProcessorAssistedFactory: DeleteInstructionsProcessorAssistedFactory,
){
    // FIXME: верен ли порядок?
    suspend fun processFileInstructions(isUnprocessed: Boolean) {
        // 10 - создание каталогов
        dirCreationInstructionsProcessor.process(list(isUnprocessed))

        // 11 - копирование файлов
        copyInstructionsProcessor.process(list(isUnprocessed))

        // 20 - разрешение коллизий
//        collisionResolverInstructionsProcessor.process(list(isUnprocessed))

        // 30 - бекап
        backupInstructionsProcessor.process(list(isUnprocessed))

        // 40 - удаление
        deleteInstructionsProcessor.process(list(isUnprocessed))
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
        deleteInstructionsProcessorAssistedFactory.create(syncTask, executionId, parentScope)
    }

    private val dirCreationInstructionsProcessor by lazy {
        dirCreationInstructionsProcessorAssistedFactory.create(syncTask,executionId,parentScope)
    }

    private val copyInstructionsProcessor by lazy {
        fileCopyInstructionsProcessorAssistedFactory.create(syncTask, executionId, parentScope)
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