package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.VirtualSyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.VirtualSyncObjectAdderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.absolutePathOfSide
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.FileOperation
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.exceptions.SyncObjectNotFoundException
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.DatabaseFileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll

class FileCopyInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val syncTask: SyncTask,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    fileOperationLogger: DatabaseFileOperationLogger,
    syncInstructionUpdater: SyncInstructionUpdater,
    syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectCopierFactory: SyncObjectFileCopierAssistedFactory,
    private val virtualSyncObjectAdderAssistedFactory: VirtualSyncObjectAdderAssistedFactory, // Это мне не нравится...
)
    : CommonFileInstructionsProcessor(
        fileOperationLogger, syncInstructionUpdater, syncObjectDBReader)
{
    suspend fun process(list: Iterable<FileInstruction>) {
        processReal(
            list
                .filter { it.isCopying }
                .filter { it.isFile }
        )
    }

    private suspend fun processReal(list: Iterable<FileInstruction>) {
        copyFromSourceToTarget(list.filter { FileOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation })
        copyFromTargetToSource(list.filter { FileOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation })
    }


    private suspend fun copyFromSourceToTarget(list: Iterable<FileInstruction>) {
        list.map { instruction ->

            val sourceObjectId = instruction.objectIdInSource

            if (null == sourceObjectId)
                throw IllegalArgumentException("Source object id cannot be null, but is in ${FileInstruction.TAG}: $instruction")

            copyFromTo(
                sourceObjectId,
                SyncSide.TARGET,
                R.string.LOG_ITEM_copying_from_source_to_target
            ).also {
                markInstructionAsProcessed(instruction)
            }

        }.joinAll()
    }


    private suspend fun copyFromTargetToSource(list: Iterable<FileInstruction>) {
        list.map { instruction ->

            val targetObjectId = instruction.objectIdInTarget

            if (null == targetObjectId)
                throw IllegalArgumentException("Target object id (from that to be copying to source) cannot be null, but is in ${FileInstruction.TAG}: $instruction")

            copyFromTo(
                targetObjectId,
                SyncSide.SOURCE,
                R.string.LOG_ITEM_copying_from_target_to_source
            )
        }.joinAll()
    }


    @Throws(SyncObjectNotFoundException::class)
    private suspend fun copyFromTo(
        fromObjectId: String,
        toSide: SyncSide,
        @StringRes operationName: Int,
    ): Job {
        val fromObject = getObjectOrFail(fromObjectId)

        val fromPath = fromObject.absolutePathIn(syncTask)
        val toPath = fromObject.absolutePathIn(syncTask.absolutePathOfSide(toSide))

        val logItemId = newRandomId
        val jobId = newRandomId

        val logBaseInfo = DatabaseFileOperationLogger.LogBaseInfo(
            taskId = syncTask.id,
            executionId = executionId,
            logItemId = logItemId,
            operationName = operationName,
            firstItem = fromPath,
            secondItem = toPath
        )

        return runInCoroutineExtended(
            scope = parentScope,
            onStart = { logStarted(logBaseInfo, jobId = jobId) },
            onFinish = { logFinished(logBaseInfo) },
            onCancel = { logCancelled(logBaseInfo, it) },
            onError = { logError(logBaseInfo, it) },
        ) {
            syncObjectCopier.copyFileFromSourceToTarget(
                syncObject = fromObject,
                absolutePathInTarget = toPath,
                overwriteIfExists = true, // FIXME: убрать!
            ) { transferredBytes: Long ->

            }

            virtualSyncObjectAdder.actualizeInfoAboutObject(
                correspondingObject = fromObject,
                syncSide = toSide,
                syncState = ExecutionState.SUCCESS,
            )
        }
    }

    private val syncObjectCopier by lazy {
        syncObjectCopierFactory.create(syncTask)
    }

    private val virtualSyncObjectAdder: VirtualSyncObjectAdder by lazy {
        virtualSyncObjectAdderAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface FileCopyInstructionsProcessorAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) syncTask: SyncTask,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
        parentScope: CoroutineScope,
    ): FileCopyInstructionsProcessor
}