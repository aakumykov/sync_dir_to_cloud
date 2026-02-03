package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base.BasicInstructionsProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class DirCreationInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val dirCreatorAssistedFactory: DirCreator5AssistedFactory,
    private val basicInstructionsProcessorAssistedFactory: BasicInstructionsProcessorAssistedFactory,
) {
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(list.filter { it.isCopying }.filter { it.isDir })
    }


    private suspend fun processReal(list: Iterable<SyncInstruction>) {
       createDirsFromSourceInTarget(list.filter { SyncOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation })
       createDirsFromTargetInSource(list.filter { SyncOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation })
    }


    private suspend fun createDirsFromSourceInTarget(list: List<SyncInstruction>) {
        list.forEach { instruction ->

            val sourceObjectId = instruction.objectIdInSource

            if (null == sourceObjectId)
                throw IllegalArgumentException("Source object id cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            createDir(
                sourceObjectId,
                SyncSide.TARGET,
                R.string.LOG_ITEM_creating_dir_from_source_in_target
            )
        }
    }


    private suspend fun createDirsFromTargetInSource(list: List<SyncInstruction>) {
        list.forEach { instruction ->

            val targetObjectId = instruction.objectIdInTarget

            if (null == targetObjectId)
                throw IllegalArgumentException("Target object id cannot be null, but is in ${SyncInstruction.TAG}: $instruction")

            createDir(
                targetObjectId,
                SyncSide.SOURCE,
                R.string.LOG_ITEM_creating_dir_from_target_in_source
            )
        }
    }


    private suspend fun createDir(
        fromObjectId: String,
        inSyncSide: SyncSide,
        @StringRes operationName: Int
    ) {
        val sourceObject = getObjectOrFail(fromObjectId)

        basicInstructionsProcessor.process(
            parentScope = parentScope,
            operationName = operationName,
            firstItem = null,
            secondItem = null,
        ) {
            when(inSyncSide) {
                SyncSide.SOURCE -> {
                    dirCreator.createDirInSource(
                        basePath = sourceObject.basePathIn(syncTask.targetPath!!),
                        dirName = sourceObject.name
                    )
                }
                SyncSide.TARGET -> {
                    dirCreator.createDirInTarget(
                        basePath = sourceObject.basePathIn(syncTask.targetPath!!),
                        dirName = sourceObject.name
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