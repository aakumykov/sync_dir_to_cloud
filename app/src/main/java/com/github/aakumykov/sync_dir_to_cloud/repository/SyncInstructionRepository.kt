package com.github.aakumykov.sync_dir_to_cloud.repository

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.SyncInstructionDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO
import okhttp3.internal.toImmutableList
import javax.inject.Inject

class SyncInstructionRepository @Inject constructor(
    private val syncInstructionDAO: SyncInstructionDAO,
)
    : SyncInstructionUpdater,
    SyncInstructionReader,
    SyncInstructionDeleter
{
    companion object {
        val TAG: String = SyncInstructionRepository::class.java.simpleName
    }

    suspend fun add(fileInstruction: FileInstruction) {
        Log.d(TAG, "add($fileInstruction)")
        syncInstructionDAO.add(fileInstruction)
    }

    override suspend fun getAllFor(taskId: String, executionId: String): List<FileInstruction> {
        return syncInstructionDAO.getAllFor(taskId, executionId)
    }

    override suspend fun getAllWithoutExecutionId(taskId: String): List<FileInstruction> {
        return syncInstructionDAO.getAllWithoutExecutionId(taskId)
    }

    private suspend fun deleteInstruction(id: String) {
        syncInstructionDAO.delete(id)
    }

    override suspend fun markAsProcessed(instructionId: String) {
        syncInstructionDAO.markAsProcessed(instructionId)
    }

    override fun getSyncInstructionsForObjectInSource(syncObjectId: String): List<FileInstruction> {
        return syncInstructionDAO.getSyncInstructionsForSourceId(syncObjectId)
    }

    suspend fun deleteUnprocessedDuplicatedInstructions(taskId: String) {

        val initialList = syncInstructionDAO.getAllWithoutExecutionId(taskId).toMutableList()

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

    override suspend fun deleteFinishedInstructionsFor(taskId: String) {
        syncInstructionDAO.deleteFinishedInstructionsForTask(taskId)
    }

    override fun getSyncInstructionsFor(taskId: String): List<FileInstruction> {
        return syncInstructionDAO.getSyncInstructionsFor(taskId)
    }
}