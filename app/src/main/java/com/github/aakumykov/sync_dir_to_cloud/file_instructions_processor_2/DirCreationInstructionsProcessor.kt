package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base.BasicInstructionsProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class DirCreationInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val dirCreatorAssistedFactory: DirCreator5AssistedFactory,
    private val basicInstructionsProcessorAssistedFactory: BasicInstructionsProcessorAssistedFactory,
    private val syncInstructionUpdater: SyncInstructionUpdater,
) {
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(
            list
                .filter { it.isCopying }
                .filter { it.isDir }
        )
    }


    private suspend fun processReal(list: Iterable<SyncInstruction>) {
       createDirsFromSourceInTarget(list.filter { SyncOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation })
       createDirsFromTargetInSource(list.filter { SyncOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation })
    }


    private suspend fun createDirsFromSourceInTarget(list: List<SyncInstruction>) {
        list.map { instruction ->

            val sourceObjectId = instruction.objectIdInSource

            if (null == sourceObjectId)
                throw IllegalArgumentException("Source object id cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            createDir(
                scope = parentScope,
                sourceObjectId,
                SyncSide.TARGET,
                R.string.LOG_ITEM_creating_dir_from_source_in_target
            ).also {
                syncInstructionUpdater.markAsProcessed(instruction.id)
            }


        }.joinAll()
    }


    private suspend fun createDirsFromTargetInSource(list: List<SyncInstruction>) {
        list.map { instruction ->

            val targetObjectId = instruction.objectIdInTarget

            if (null == targetObjectId)
                throw IllegalArgumentException("Target object id cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            createDir(
                scope = parentScope,
                targetObjectId,
                SyncSide.SOURCE,
                R.string.LOG_ITEM_creating_dir_from_target_in_source
            )
        }.joinAll()
    }


    private suspend fun createDir(
        scope: CoroutineScope,
        fromObjectId: String,
        toSyncSide: SyncSide,
        @StringRes operationName: Int
    ): Job {
        val fromObject = getObjectOrFail(fromObjectId)

        val basePath = when(toSyncSide) {
            SyncSide.SOURCE -> fromObject.basePathIn(syncTask.sourcePath!!)
            SyncSide.TARGET -> fromObject.basePathIn(syncTask.targetPath!!)
        }

        return basicInstructionsProcessor.process(
            scope = scope,
            operationName = operationName,
            firstItem = fromObject.absolutePathIn(syncTask),
            secondItem = fromObject.absolutePathIn(basePath),
        ) {
            when(toSyncSide) {
                SyncSide.SOURCE -> {
                    dirCreator.createDirInSource(
                        basePath = basePath,
                        dirName = fromObject.name
                    )
                }
                SyncSide.TARGET -> {
                    dirCreator.createDirInTarget(
                        basePath = basePath,
                        dirName = fromObject.name
                    )
                }
            }
        }
    }


    // TODO: вынести в базовый класс
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

    private val dirCreator by lazy {
        dirCreatorAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface DirCreationInstructionsProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        parentScope: CoroutineScope,
    ): DirCreationInstructionsProcessor
}