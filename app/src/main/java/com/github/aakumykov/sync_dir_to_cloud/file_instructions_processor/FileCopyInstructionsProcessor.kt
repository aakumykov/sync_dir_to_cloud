package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.VirtualSyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.VirtualSyncObjectAdderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettings
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.absolutePathOfSide
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.FileOperation
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.exceptions.SyncObjectNotFoundException
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.DatabaseFileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class FileCopyInstructionsProcessor @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val syncTask: SyncTask,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    fileOperationLogger: DatabaseFileOperationLogger,
    syncInstructionUpdater: SyncInstructionUpdater,
    syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectCopierFactory: SyncObjectFileCopierAssistedFactory,
    private val virtualSyncObjectAdderAssistedFactory: VirtualSyncObjectAdderAssistedFactory, // Это мне не нравится...
    private val appSettings: AppSettings,
)
    : BasicFileInstructionsProcessor(
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
        processByChunks(appSettings.fileParallelism, list.filter { FileOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation }) {
            copyFromSourceToTarget(it)
        }
        processByChunks(appSettings.fileParallelism, list.filter { FileOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation }) {
            copyFromTargetToSource(it)
        }
    }

    private suspend fun processByChunks(
        chunkSize: Int,
        instructionList: List<FileInstruction>,
        block: suspend (oneChunk: Iterable<FileInstruction>) -> Unit
    ) {
        instructionList.chunked(chunkSize).map { oneChunk ->
            block.invoke(oneChunk)
        }
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
        val syncObject = getObjectOrFail(fromObjectId)

        val fromPath = syncObject.absolutePathIn(syncTask)
        val toPath = syncObject.absolutePathIn(syncTask.absolutePathOfSide(toSide))

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
            onStart = {
                logStarted(logBaseInfo, jobId = jobId)
                OperationJobsHolder.addJob(jobId, it)
            },
            onFinish = { logFinished(logBaseInfo) },
            onCancel = {
                Log.d(TAG, "copyFromTo(), runInCoroutineExtended() отменено: ${it.errorMsgExtended}")
                logCancelled(logBaseInfo, it)
            },
            onError = { logError(logBaseInfo, it) },
            finally = {
                OperationJobsHolder.removeJob(jobId)
            }
        ) {
            syncObjectCopier.copyFileFromSourceToTarget(
                syncObject = syncObject,
                absolutePathInTarget = toPath,
                overwriteIfExists = true, // FIXME: убрать!
            ) { transferredBytes: Long ->
                val progress = 1f * transferredBytes / syncObject.size
                parentScope.launch {
//                    Log.d(TAG, "progress: $progress")
                    updateProgress(logItemId, progress)
                }
            }

            virtualSyncObjectAdder.actualizeInfoAboutObject(
                correspondingObject = syncObject,
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

    companion object {
        val TAG: String = FileCopyInstructionsProcessor::class.java.simpleName
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