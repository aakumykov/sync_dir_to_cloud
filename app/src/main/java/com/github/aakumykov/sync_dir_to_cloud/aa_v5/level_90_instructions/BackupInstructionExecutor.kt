package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuper
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.FileOperation
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Выполняет одну инструкцию бекапа.
 */
class BackupInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectBackuperAssistedFactory: SyncObjectBackuperAssistedFactory,
) {
    suspend fun execute(fileInstruction: FileInstruction) {
        when(fileInstruction.operation) {
            FileOperation.BACKUP_IN_SOURCE -> backupInSource(fileInstruction)
            FileOperation.BACKUP_IN_TARGET -> backupInTarget(fileInstruction)
            else -> throw IllegalArgumentException("Unsupported operation: '$fileInstruction'")
        }
    }

    suspend fun backupInSource(fileInstruction: FileInstruction) {
        val objectId = fileInstruction.objectIdInSource
            ?: throw IllegalStateException("Sync instruction has no object id in source.")

        backup(objectId)
    }


    suspend fun backupInTarget(fileInstruction: FileInstruction) {
        val objectId = fileInstruction.objectIdInTarget
            ?: throw IllegalStateException("Sync instruction has no object id in target.")

        backup(objectId)
    }


    private suspend fun backup(objectId: String) {
        val syncObject = syncObjectDBReader.getSyncObject(objectId)
            ?: throw RuntimeException("There is no object with id='$objectId'")

        syncObjectBackuper3.backupSyncObject(syncObject)
    }


    private val syncObjectBackuper3: SyncObjectBackuper by lazy {
        syncObjectBackuperAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface BackupInstructionExecutorAssistedFactory {
    fun create(syncTask: SyncTask): BackupInstructionExecutor
}