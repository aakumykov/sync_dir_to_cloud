package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO6
import okhttp3.internal.toImmutableList
import javax.inject.Inject

class SyncInstructionRepository @Inject constructor(
    private val syncInstructionDAO6: SyncInstructionDAO6,
)
    : SyncInstructionUpdater,
    SyncInstructionReader
{
    suspend fun add(syncInstruction: SyncInstruction) {
        syncInstructionDAO6.add(syncInstruction)
    }

    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction> {
        return syncInstructionDAO6.getAllFor(taskId, executionId)
    }

    suspend fun getAllWithoutExecutionId(taskId: String): List<SyncInstruction> {
        return syncInstructionDAO6.getAllWithoutExecutionId(taskId)
    }

    suspend fun deleteFinishedInstructionsForTask(taskId: String) {
        syncInstructionDAO6.deleteFinishedInstructionsForTask(taskId)
    }

    suspend fun deleteInstruction(id: String) {
        syncInstructionDAO6.delete(id)
    }

    override suspend fun markAsProcessed(instructionId: String) {
        syncInstructionDAO6.markAsProcessed(instructionId)
    }

    suspend fun deleteUnprocessedDuplicatedInstructions(taskId: String) {

        val initialList = syncInstructionDAO6.getAllWithoutExecutionId(taskId).toMutableList()

        initialList.toImmutableList()
            .distinctBy {
                "${it.isDir}:${it.relativePath}:${it.operation}"
            }.also { uniqueOperationsList ->
//                Log.d("TAG", uniqueOperationsList.toString())
                initialList.removeAll(uniqueOperationsList)
            }

        initialList.forEach {
            deleteInstruction(it.id)
        }
    }

    override fun getSyncInstructionsFor(taskId: String): List<SyncInstruction> {
        return syncInstructionDAO6.getSyncInstructionsFor(taskId)
    }
}