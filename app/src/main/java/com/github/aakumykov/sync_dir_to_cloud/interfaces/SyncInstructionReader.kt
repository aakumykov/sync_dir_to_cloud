package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction

interface SyncInstructionReader {
    fun getSyncInstructionsFor(taskId: String): List<FileInstruction>
    suspend fun getAllFor(taskId: String, executionId: String): List<FileInstruction>
    suspend fun getAllWithoutExecutionId(taskId: String): List<FileInstruction>
    fun getSyncInstructionsForObjectInSource(syncObjectId: String): List<FileInstruction>
}