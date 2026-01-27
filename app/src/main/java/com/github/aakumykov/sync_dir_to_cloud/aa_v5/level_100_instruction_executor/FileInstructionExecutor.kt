package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_100_instruction_executor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import javax.inject.Inject

abstract class FileInstructionExecutor @Inject constructor(
    private val fileOperationLogger2: FileOperationLogger2
) {

    abstract suspend fun execute(syncInstruction: SyncInstruction)

    abstract fun onStartExecuting()
}

class BackupFileInstructionExecutor : FileInstructionExecutor() {

    override suspend fun execute(syncInstruction: SyncInstruction) {
        TODO("Not yet implemented")
    }

    override fun onStartExecuting() {
        TODO("Not yet implemented")
    }
}