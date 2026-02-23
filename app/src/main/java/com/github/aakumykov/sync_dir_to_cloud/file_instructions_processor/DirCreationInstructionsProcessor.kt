package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.FileOperation
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
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

class DirCreationInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    fileOperationLogger: DatabaseFileOperationLogger,
    syncInstructionUpdater: SyncInstructionUpdater,
    syncObjectDBReader: SyncObjectDBReader,
    private val dirCreatorAssistedFactory: DirCreator5AssistedFactory,
)
    : BasicFileInstructionsProcessor(
        fileOperationLogger, syncInstructionUpdater, syncObjectDBReader)
{
    suspend fun process(list: Iterable<FileInstruction>) {
        processReal(
            list
                .filter { it.isCopying }
                .filter { it.isDir }
        )
    }


    private suspend fun processReal(list: Iterable<FileInstruction>) {
       createDirsFromSourceInTarget(list.filter { FileOperation.COPY_FROM_SOURCE_TO_TARGET == it.operation })
       createDirsFromTargetInSource(list.filter { FileOperation.COPY_FROM_TARGET_TO_SOURCE == it.operation })
    }


    private suspend fun createDirsFromSourceInTarget(list: List<FileInstruction>) {
        list.map { instruction ->

            val sourceObjectId = instruction.objectIdInSource

            if (null == sourceObjectId)
                throw IllegalArgumentException("Source object id cannot be null, but is in ${FileInstruction.TAG}: $instruction")

            createDir(
                scope = parentScope,
                sourceObjectId,
                SyncSide.TARGET,
                R.string.LOG_ITEM_creating_dir_from_source_in_target
            ).also {
                markInstructionAsProcessed(instruction)
            }


        }.joinAll()
    }


    private suspend fun createDirsFromTargetInSource(list: List<FileInstruction>) {
        list.map { instruction ->

            val targetObjectId = instruction.objectIdInTarget

            if (null == targetObjectId)
                throw IllegalArgumentException("Target object id cannot be null, but is in ${FileInstruction.TAG}: $instruction")

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

        val firstItem = fromObject.absolutePathIn(syncTask)
        val secondItem = fromObject.absolutePathIn(basePath)

        val logItemId = newRandomId
        val jobId = newRandomId

        val logBaseInfo = DatabaseFileOperationLogger.LogBaseInfo(
            taskId = syncTask.id,
            executionId = executionId,
            logItemId = logItemId,
            operationName = operationName,
            firstItem = firstItem,
            secondItem = secondItem
        )

        return runInCoroutineExtended(
            scope = parentScope,
            onStart = {
                logStarted(logBaseInfo, jobId = jobId)
                OperationJobsHolder.addJob(jobId, it)
            },
            onFinish = { logFinished(logBaseInfo) },
            onCancel = { logCancelled(logBaseInfo, it) },
            onError = { logError(logBaseInfo, it) },
            finally = {
                OperationJobsHolder.removeJob(jobId)
            }
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