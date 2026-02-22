package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.FileAndDirDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.FileAndDirDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.FileOperation
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.launchWithStartCallback
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class DeleteInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val fileAndDirDeleterAssistedFactory: FileAndDirDeleterAssistedFactory,
) {
    suspend fun execute(syncInstruction: SyncInstruction) {
        val jobId = newRandomId
        parentScope.launchWithStartCallback(onStart = { job ->
            OperationJobsHolder.addJob(jobId, job)
        }) {
            when(syncInstruction.operation) {
                FileOperation.DELETE_IN_SOURCE -> deleteInSource(syncInstruction)
                FileOperation.DELETE_IN_TARGET -> deleteInTarget(syncInstruction)
                else -> throw IllegalArgumentException("Unsupported operation: '$syncInstruction'")
            }
        }.join()
    }

    private suspend fun deleteInSource(syncInstruction: SyncInstruction) {
        val syncObject = syncObjectDBReader.getSyncObject(syncInstruction.objectIdInSource!!)

        if (syncInstruction.isDir) fileAndDirDeleter.deleteEmptyDirInSource(syncObject!!)
        else fileAndDirDeleter.deleteFileInSource(syncObject!!)
    }

    private suspend fun deleteInTarget(syncInstruction: SyncInstruction) {
        val syncObject = syncObjectDBReader.getSyncObject(syncInstruction.objectIdInTarget!!)

        if (syncInstruction.isDir) fileAndDirDeleter.deleteEmptyDirInTarget(syncObject!!)
        else fileAndDirDeleter.deleteFileInTarget(syncObject!!)
    }

    private val fileAndDirDeleter: FileAndDirDeleter by lazy {
        fileAndDirDeleterAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface DeleteInstructionExecutorAssistedFactory {
    fun create(syncTask: SyncTask,
               scope: CoroutineScope): DeleteInstructionExecutor
}