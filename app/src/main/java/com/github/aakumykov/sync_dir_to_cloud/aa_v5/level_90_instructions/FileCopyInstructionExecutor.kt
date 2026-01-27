package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FileCopyInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val parentScope: CoroutineScope,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncInstructionUpdater: SyncInstructionUpdater,
    private val syncObjectDeleterAssistedFactory: SyncObjectDeleterAssistedFactory5,
) {
    suspend fun execute(syncInstruction: SyncInstruction) {

        if (syncInstruction.isDir)
            throw IllegalArgumentException("$TAG cannot work with dirs, only with files!")

        parentScope.launch {
            when(syncInstruction.operation) {
                SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> copyFromSourceToTarget(syncInstruction)
                SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> copyFromTargetToSource(syncInstruction)
                else -> throw IllegalArgumentException("Unsupported operation: '$syncInstruction'")
            }
        }.join()
    }

    private suspend fun copyFromSourceToTarget(syncInstruction: SyncInstruction) {
        val syncObject = syncObjectDBReader.getSyncObject(syncInstruction.objectIdInSource!!)

        if (syncInstruction.isDir) syncObjectDeleter.deleteEmptyDirInSource(syncObject!!)
        else syncObjectDeleter.deleteFileInSource(syncObject!!)

        markInstructionAsProcessed(syncInstruction)
    }

    private suspend fun copyFromTargetToSource(syncInstruction: SyncInstruction) {
        val syncObject = syncObjectDBReader.getSyncObject(syncInstruction.objectIdInTarget!!)

        if (syncInstruction.isDir) syncObjectDeleter.deleteEmptyDirInTarget(syncObject!!)
        else syncObjectDeleter.deleteFileInTarget(syncObject!!)

        markInstructionAsProcessed(syncInstruction)
    }

    private suspend fun markInstructionAsProcessed(syncInstruction: SyncInstruction) {
        syncInstructionUpdater.markAsProcessed(syncInstruction.id)
    }

    private val syncObjectDeleter: SyncObjectDeleter5 by lazy {
        syncObjectDeleterAssistedFactory.create(syncTask)
    }

    companion object {
        val TAG: String = FileCopyInstructionExecutor::class.java.simpleName
    }
}

@AssistedFactory
interface FileCopyInstructionExecutorAssistedFactory {
    fun create(syncTask: SyncTask, scope: CoroutineScope): FileCopyInstructionExecutor
}