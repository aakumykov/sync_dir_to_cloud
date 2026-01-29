package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
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
) {
    suspend fun work(scope: CoroutineScope, instruction: SyncInstruction) {

        val sourceObject = syncObjectDBReader.getSyncObject(instruction.objectIdInSource!!)
        val targetObject = syncObjectDBReader.getSyncObject(instruction.objectIdInTarget!!)

        val pathInTarget = targetObject!!.absolutePathIn(syncTask)

        val operationNameId = when(instruction.operation) {
            SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> R.string.LOG_ITEM_copying_from_source_to_target
            SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> R.string.LOG_ITEM_copying_from_source_to_target
            else -> throw IllegalArgumentException("Unsupported operation '${instruction.operation}'")
        }

        val relativeFilePath = sourceObject!!.relativePath

        basicInstructionsProcessor.process(
            scope = scope,
            operationNameId = operationNameId,
            relativeFilePath = relativeFilePath,
        ){
            syncObjectCopier.copyFileFromSourceToTarget(
                sourceObject,
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
}


@AssistedFactory
interface CopyInstructionsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): CopyInstructionsProcessor
}