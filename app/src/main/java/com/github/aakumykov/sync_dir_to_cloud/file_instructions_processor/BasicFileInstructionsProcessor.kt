package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.exceptions.SyncObjectNotFoundException
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationLogProgressUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.FileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.DatabaseFileOperationLogger

abstract class BasicFileInstructionsProcessor (
    private val databaseFileOperationLogger: DatabaseFileOperationLogger,
    private val syncInstructionUpdater: SyncInstructionUpdater,
    private val syncObjectDBReader: SyncObjectDBReader,
)
    : FileOperationLogger by databaseFileOperationLogger,
    FileOperationLogProgressUpdater by databaseFileOperationLogger
{
    suspend fun markInstructionAsProcessed(fileInstruction: FileInstruction) {
        syncInstructionUpdater.markAsProcessed(fileInstruction.id)
    }

    @Throws(SyncObjectNotFoundException::class)
    protected suspend fun getObjectOrFail(objectId: String): SyncObject {
        return syncObjectDBReader.getSyncObject(objectId)
            .let {
                if (null == it)
                    throwNoObjectWithId(objectId)
                it!!
            }
    }

    @Throws(SyncObjectNotFoundException::class)
    protected fun throwNoObjectWithId(objectId: String) {
        throw SyncObjectNotFoundException(objectId)
    }
}
