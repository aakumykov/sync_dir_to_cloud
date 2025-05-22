package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuper3
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuper3AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

//
// Задача Исполнителя инструкций - выбрать объекты для работы
// согласно инструкции, отправить их на выполнение.
// Реагировать на аномалии (как?).
//
class BackupInstructionExecutor2 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectBackuper3AssistedFactory: SyncObjectBackuper3AssistedFactory,
    private val syncInstructionUpdater: SyncInstructionUpdater,
) {
    /*suspend fun execute(syncInstruction: SyncInstruction) {
        when(syncInstruction.operation) {
            SyncOperation.BACKUP_IN_SOURCE -> backupInSource(syncInstruction)
            SyncOperation.BACKUP_IN_TARGET -> backupInTarget(syncInstruction)
            else -> throw IllegalArgumentException("Unsupported operation: '$syncInstruction'")
        }
    }*/

    suspend fun backupInSource(syncInstruction: SyncInstruction) {
        val objectId = syncInstruction.objectIdInSource
            ?: throw IllegalStateException("Sync instruction has no object id in source.")
        backupAndMarkAsProcessed(objectId, syncInstruction)
    }


    suspend fun backupInTarget(syncInstruction: SyncInstruction) {
        val objectId = syncInstruction.objectIdInTarget
            ?: throw IllegalStateException("Sync instruction has no object id in target.")
        backupAndMarkAsProcessed(objectId, syncInstruction)
    }


    private suspend fun backupAndMarkAsProcessed(objectId: String, syncInstruction: SyncInstruction) {

        val syncObject = syncObjectDBReader.getSyncObject(objectId)
            ?: throw RuntimeException("There is no object with id='$objectId'")

        syncObjectBackuper3.backupSyncObject(syncObject)

        syncInstructionUpdater.markAsProcessed(syncInstruction.id)
    }


    private val syncObjectBackuper3: SyncObjectBackuper3 by lazy {
        syncObjectBackuper3AssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface BackupInstructionExecutor2AssistedFactory {
    fun create(syncTask: SyncTask): BackupInstructionExecutor2
}