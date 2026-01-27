package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.launchWithStartCallback
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class DeleteInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectDeleterAssistedFactory: SyncObjectDeleterAssistedFactory5,
    private val syncInstructionUpdater: SyncInstructionUpdater,
    private val operationJobHolder: OperationJobsHolder,
) {
    suspend fun execute(syncInstruction: SyncInstruction) {
        val jobId = newRandomId
        parentScope.launchWithStartCallback(onStart = { job ->
            operationJobHolder.addJob(jobId, job)
            logger.logStarted(jobId,, )
        }) {
            when(syncInstruction.operation) {
                SyncOperation.DELETE_IN_SOURCE -> deleteInSource(syncInstruction)
                SyncOperation.DELETE_IN_TARGET -> deleteInTarget(syncInstruction)
                else -> throw IllegalArgumentException("Unsupported operation: '$syncInstruction'")
            }
        }.join()
    }

    private suspend fun deleteInSource(syncInstruction: SyncInstruction) {
        val syncObject = syncObjectDBReader.getSyncObject(syncInstruction.objectIdInSource!!)

        if (syncInstruction.isDir) syncObjectDeleter.deleteEmptyDirInSource(syncObject!!)
        else syncObjectDeleter.deleteFileInSource(syncObject!!)

        markInstructionAsProcessed(syncInstruction)
    }

    private suspend fun deleteInTarget(syncInstruction: SyncInstruction) {
        val syncObject = syncObjectDBReader.getSyncObject(syncInstruction.objectIdInTarget!!)
        if (syncInstruction.isDir) syncObjectDeleter.deleteEmptyDirInTarget(syncObject!!)
        else syncObjectDeleter.deleteFileInTarget(syncObject!!)
        markInstructionAsProcessed(syncInstruction)
    }

    private suspend fun markInstructionAsProcessed(syncInstruction: SyncInstruction) {
        syncInstructionUpdater.markAsProcessed(syncInstruction.id)
    }

    private val syncObjectDeleter: SyncObjectDeleter5 by lazy {
        syncObjectDeleterAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface DeleteInstructionExecutorAssistedFactory {
    fun create(syncTask: SyncTask,
               executionId: String,
               scope: CoroutineScope): DeleteInstructionExecutor
}