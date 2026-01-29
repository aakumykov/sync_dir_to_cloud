package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

// TODO: переименовать в FileCopyInstructionsProcessor
class CopyInstructionsProcessor @AssistedInject constructor(
    private val syncTask: SyncTask,
    private val executionId: String,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectCopierFactory: SyncObjectFileCopierAssistedFactory,
    private val basicInstructionsProcessor: BasicInstructionsProcessor,
)
    : FileOperationLogMessageSupplier
{
    suspend fun work(scope: CoroutineScope, instruction: SyncInstruction) {

        val sourceObject = syncObjectDBReader.getSyncObject(instruction.objectIdInSource!!)
        val targetObject = syncObjectDBReader.getSyncObject(instruction.objectIdInTarget!!)

        val pathInTarget = targetObject!!.absolutePathIn(syncTask)

        basicInstructionsProcessor.process(scope, this) {

            syncObjectCopier.copyFileFromSourceToTarget(
                sourceObject!!,
                pathInTarget,
                true // FIXME: убрать!
            )
        }
    }

    private val syncObjectCopier by lazy {
        syncObjectCopierFactory.create(
            syncTask = syncTask,
            executionId = executionId,
            databaseInteractingScope = CoroutineScope(Dispatchers.IO)
        )
    }

    override val operationMessageIdStarted: Int
        get() = R.string.LOG_ITEM_copying_file_started
    
    override val operationMessageIdFinished: Int
        get() = R.string.LOG_ITEM_copying_file_finished
    
    override val operationMessageIdCancelled: Int
        get() = R.string.LOG_ITEM_copying_file_cancelled
    
    override val operationMessageIdError: Int
        get() = R.string.LOG_ITEM_copying_file_error
    
    override val operationDescriptionStarted: String
        get() = TODO("Not yet implemented")
    
    override val operationDescriptionFinished: String
        get() = TODO("Not yet implemented")
    
    override val operationDescriptionCancel: String?
        get() = TODO("Not yet implemented")
    
    override val operationDescriptionError: String?
        get() = TODO("Not yet implemented")
}


@AssistedFactory
interface CopyInstructionsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): CopyInstructionsProcessor
}