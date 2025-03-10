package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuper5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuperAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectRenamerAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list.SyncObjectListChunkedCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class OneSyncInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val syncOptions: SyncOptions,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
    private val syncObjectListChunkedCopierAssistedFactory5: SyncObjectListChunkedCopierAssistedFactory5,
    private val syncObjectDeleterAssistedFactory5: SyncObjectDeleterAssistedFactory5,
    private val syncObjectDeleterWithBackupAssistedFactory5: SyncObjectDeleterWithBackupAssistedFactory5,
    private val backuperAssistedFactory5: SyncObjectBackuperAssistedFactory5,
    private val renamerAssistedFactory5: SyncObjectRenamerAssistedFactory5,
    private val syncObjectCopierWithBackupAssistedFactory5: SyncObjectCopierWithBackupAssistedFactory5,
    private val mutualRenamerAndCopierAssistedFactory5: MutualRenamerAndCopierAssistedFactory5,
){
    suspend fun execute(instruction: SyncInstruction6) {
        when(instruction.operation) {
            SyncOperation6.RENAME_COLLISION_IN_SOURCE -> renameCollisionInSource(instruction.objectIdInSource!!)
            SyncOperation6.RENAME_COLLISION_IN_TARGET -> renameCollisionInTarget(instruction.objectIdInTarget!!)

            SyncOperation6.COPY_FROM_SOURCE_TO_TARGET -> copyFromSourceToTarget(instruction.objectIdInSource!!)
            SyncOperation6.COPY_FROM_TARGET_TO_SOURCE -> copyFromTargetToSource(instruction.objectIdInTarget!!)

            SyncOperation6.DELETE_IN_SOURCE -> deleteInSource(instruction.objectIdInSource!!)
            SyncOperation6.DELETE_IN_TARGET -> deleteInTarget(instruction.objectIdInTarget!!)

            SyncOperation6.BACKUP_IN_SOURCE -> { /*backuper.backupInSource(instruction)*/ }
            SyncOperation6.BACKUP_IN_TARGET -> { /*backuper.backupInTarget(instruction)*/ }
        }
    }

    private suspend fun renameCollisionInSource(sourceObjectId: String) {
        syncObjectReader.getSyncObject(sourceObjectId)?.also {
            renamer.renameCollisionInSource(it)
        }
    }

    private suspend fun renameCollisionInTarget(targetObjectId: String) {
        syncObjectReader.getSyncObject(targetObjectId)?.also {
            renamer.renameCollisionInTarget(it)
        }
    }

    private suspend fun copyFromSourceToTarget(sourceObjectId: String) {
        syncObjectReader.getSyncObject(sourceObjectId)?.also {
            copier.copyFromSourceToTarget(it, syncOptions.overwriteIfExists)
            syncObjectStateChanger.setSyncState(sourceObjectId, ExecutionState.SUCCESS)
        } ?: {
//            TODO: errorLogger.log()
        }
    }

    private suspend fun copyFromTargetToSource(targetObjectId: String) {
        syncObjectReader.getSyncObject(targetObjectId)?.also {
            copier.copyFromTargetToSource(it, syncOptions.overwriteIfExists)
            syncObjectStateChanger.setSyncState(targetObjectId, ExecutionState.SUCCESS)
        } ?: run {
            // TODO: где и как регистриро БРОСАТЬ ИСКЛЮЧЕНИЕ
        }
    }


    /**
     * // FIXME: удалять сначала файлы, потом каталоги...
     * // Это делается в [SyncInstructionsProcessor6.processInstructions]
     */
    private suspend fun deleteInSource(sourceObjectId: String) {
        syncObjectReader.getSyncObject(sourceObjectId)?.also {
            if (it.isDir) deleter.deleteEmptyDirInSource(it)
            else deleter.deleteFileInSource(it)
        } // TODO: ?: throw Exception
    }

    private suspend fun deleteInTarget(targetObjectId: String) {
        syncObjectReader.getSyncObject(targetObjectId)?.also {
            if (it.isDir) deleter.deleteEmptyDirInTarget(it)
            else deleter.deleteFileInTarget(it)
        } // TODO: ?: throw Exception
    }



    private val copier: SyncObjectCopier5 by lazy {
        syncObjectCopierAssistedFactory5.create(syncTask, executionId)
    }

    private val copierWithBackup: SyncObjectCopierWithBackup5 by lazy {
        syncObjectCopierWithBackupAssistedFactory5.create(syncTask, executionId)
    }

    private val chunkedCopier by lazy {
        syncObjectListChunkedCopierAssistedFactory5.create(
            syncTask = syncTask,
            executionId = executionId,
            chunkSize = syncOptions.chunkSize,
            scope = scope,
        )
    }

    private val backuper: SyncObjectBackuper5 by lazy {
        backuperAssistedFactory5.create(syncTask, executionId)
    }

    private val deleter: SyncObjectDeleter5 by lazy {
        syncObjectDeleterAssistedFactory5.create(syncTask, executionId)
    }

    private val deleterWithBackup: SyncObjectDeleterWithBackup5 by lazy {
        syncObjectDeleterWithBackupAssistedFactory5.create(syncTask, executionId)
    }

    private val renamer by lazy {
        renamerAssistedFactory5.create(syncTask)
    }

    private val mutualRenamerAndCopier: MutualRenamerAndCopier5 by lazy {
        mutualRenamerAndCopierAssistedFactory5.create(syncTask, executionId)
    }
}


@AssistedFactory
interface SyncInstructionExecutorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        scope: CoroutineScope): OneSyncInstructionExecutor
}