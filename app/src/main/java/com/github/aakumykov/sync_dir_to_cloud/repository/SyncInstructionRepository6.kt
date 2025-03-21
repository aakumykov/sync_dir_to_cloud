package com.github.aakumykov.sync_dir_to_cloud.repository

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO6
import okhttp3.internal.toImmutableList
import javax.inject.Inject

class SyncInstructionRepository6 @Inject constructor(
    private val syncInstructionDAO6: SyncInstructionDAO6,
)
    : SyncInstructionUpdater
{
    suspend fun add(syncInstruction6: SyncInstruction6) {
        syncInstructionDAO6.add(syncInstruction6)
    }

    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction6> {
        return syncInstructionDAO6.getAllFor(taskId, executionId)
    }

    suspend fun getAllWithoutExecutionId(taskId: String): List<SyncInstruction6> {
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
}