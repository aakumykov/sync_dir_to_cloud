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
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base.BasicInstructionsProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class FileCopyInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectCopierFactory: SyncObjectFileCopierAssistedFactory,
    private val basicInstructionsProcessorAssistedFactory: BasicInstructionsProcessorAssistedFactory,
    private val syncInstructionUpdater: SyncInstructionUpdater,
) {
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(
            list
                .filter { it.isCopying }
                .filter { it.isFile }
        )
    }

    private suspend fun processReal(list: Iterable<SyncInstruction>) {
        copyFromSourceToTarget(list.filter { SyncOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation })
        copyFromTargetToSource(list.filter { SyncOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation })
    }


    private suspend fun copyFromSourceToTarget(list: Iterable<SyncInstruction>) {
        list.map { instruction ->

            val sourceObjectId = instruction.objectIdInSource

            if (null == sourceObjectId)
                throw IllegalArgumentException("Source object id cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            copyFromTo(
                sourceObjectId,
                SyncSide.TARGET,
                R.string.LOG_ITEM_copying_from_source_to_target
            ).also {
                syncInstructionUpdater.markAsProcessed(instruction.id)
            }

        }.joinAll()
    }


    private suspend fun copyFromTargetToSource(list: Iterable<SyncInstruction>) {
        list.map { instruction ->

            val targetObjectId = instruction.objectIdInTarget

            if (null == targetObjectId)
                throw IllegalArgumentException("Target object id (from that to be copying to source) cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            copyFromTo(
                targetObjectId,
                SyncSide.SOURCE,
                R.string.LOG_ITEM_copying_from_target_to_source
            )
        }.joinAll()
    }


    private suspend fun copyFromTo(
        fromObjectId: String,
        toSide: SyncSide,
        @StringRes operationName: Int,
    ): Job {
        val fromObject = getObjectOrFail(fromObjectId)

        val sourcePath = fromObject.absolutePathIn(syncTask)
        val targetPath = fromObject.absolutePathIn(syncTask.absolutePathOfSide(toSide))

        return basicInstructionsProcessor.process(
            scope = parentScope,
            operationName = operationName,
            firstItem = sourcePath,
            secondItem = targetPath,
        ){
            syncObjectCopier.copyFileFromSourceToTarget(
                fromObject,
                targetPath,
                true // FIXME: убрать!
            )
        }
    }


    private suspend fun getObjectOrFail(objectId: String): SyncObject {
        return syncObjectDBReader.getSyncObject(objectId)
            .let {
                if (null == it)
                    throwNoObjectWithId(objectId)
                it!!
            }
    }

    private fun throwNoObjectWithId(objectId: String) {
        throw IllegalStateException("${SyncObject.TAG} with id '$objectId' not found.")
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
interface FileCopyInstructionsProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        parentScope: CoroutineScope,
    ): FileCopyInstructionsProcessor
}