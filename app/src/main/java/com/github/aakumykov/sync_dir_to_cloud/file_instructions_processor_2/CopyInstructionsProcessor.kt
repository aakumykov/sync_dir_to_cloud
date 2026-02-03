package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.absolutePathOfSide
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CopyInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectCopierFactory: SyncObjectFileCopierAssistedFactory,
    private val basicInstructionsProcessorAssistedFactory: BasicInstructionsProcessorAssistedFactory,
) {
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(list.filter { it.isCopying })
    }

    private suspend fun processReal(list: Iterable<SyncInstruction>) {
        processList(list.filter { it.isDir })
        processList(list.filter { it.isFile })
    }

    private suspend fun processList(list: Iterable<SyncInstruction>) {
        copyFromSourceToTarget(list.filter { SyncOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation })
        copyFromTargetToSource(list.filter { SyncOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation })
    }
    
    private suspend fun copyFromSourceToTarget(list: Iterable<SyncInstruction>) {
        list.forEach { instruction ->

            val sourceObjectId = instruction.objectIdInSource

            if (null == sourceObjectId)
                throw IllegalArgumentException("Source object id cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            copyFromTo(
                sourceObjectId,
                SyncSide.TARGET,
                R.string.LOG_ITEM_copying_from_source_to_target
            )
        }
    }

    private suspend fun copyFromTargetToSource(list: Iterable<SyncInstruction>) {
        list.forEach { instruction ->

            val targetObjectId = instruction.objectIdInTarget

            if (null == targetObjectId)
                throw IllegalArgumentException("Target object id (from that to be copying to source) cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            copyFromTo(
                targetObjectId,
                SyncSide.SOURCE,
                R.string.LOG_ITEM_copying_from_target_to_source
            )
        }
    }

    private suspend fun copyFromTo(
        fromObjectId: String,
        targetSide: SyncSide,
        @StringRes operationName: Int,
    ) {
        val fromObject = syncObjectDBReader.getSyncObject(fromObjectId)

        if (null == fromObject)
            throw IllegalStateException("${SyncObject.TAG} used ad source of copied data is null!")

        // Ну и навертел...
        val targetPath = fromObject.absolutePathIn(syncTask.absolutePathOfSide(targetSide))

        basicInstructionsProcessor.process(
            parentScope = parentScope,
            operationName = operationName,
            firstItem = fromObject.relativePath,
            secondItem = targetPath,
        ){
            syncObjectCopier.copyFileFromSourceToTarget(
                fromObject,
                targetPath,
                true // FIXME: убрать!
            )
        }
    }

    private val basicInstructionsProcessor by lazy {
        basicInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
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
    fun create(
        syncTask: SyncTask,
        executionId: String,
        parentScope: CoroutineScope,
    ): CopyInstructionsProcessor
}