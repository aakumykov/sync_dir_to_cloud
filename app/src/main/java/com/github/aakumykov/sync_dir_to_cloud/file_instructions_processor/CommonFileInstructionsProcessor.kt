package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
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
class CommonFileInstructionsProcessor @AssistedInject constructor(
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
    // FIXME: верен ли порядок? Нет, не верен: бекапить нужно раньше, чем копировать(!)
    suspend fun processFileInstructions(isUnprocessed: Boolean) {
        // TODO: что раньше: разрешение коллизий или бекап:
        //  Разрешение коллизий - отчасти тоже своеобразный бекап...

        // Разрешение коллизий
//        collisionResolverInstructionsProcessor.process(list(isUnprocessed))

        // Бекап
        backupInstructionsProcessor.process(list(isUnprocessed))

        // Создание каталогов
        dirCreationInstructionsProcessor.process(list(isUnprocessed))

        // Копирование файлов
        copyInstructionsProcessor.process(list(isUnprocessed))

        // TODO: удалять перед копированием или после - можно (и нужно) настраивать.
        // Удаление
        deleteInstructionsProcessor.process(list(isUnprocessed))
    }

    private suspend fun list(selectUnprocessed: Boolean): Iterable<FileInstruction> {
        return if (selectUnprocessed) getNonProcessedInstructionsForTask()
        else getNonProcessedSyncInstructionsForTaskAndExecution()
    }

    private suspend fun getNonProcessedInstructionsForTask(): Iterable<FileInstruction> {
        return syncInstructionRepository
            .getAllWithoutExecutionId(syncTask.id)
            .filter { it.notProcessed }
    }

    private suspend fun getNonProcessedSyncInstructionsForTaskAndExecution(): Iterable<FileInstruction> {
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
interface FileInstructionsProcessorAssistedFactory {
    fun create(
        scope: CoroutineScope,
        syncTask: SyncTask,
        executionId: String,
    ): CommonFileInstructionsProcessor
}