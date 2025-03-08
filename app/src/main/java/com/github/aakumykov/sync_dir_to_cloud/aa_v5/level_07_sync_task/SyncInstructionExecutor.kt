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
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class SyncInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val syncOptions: SyncOptions,
    private val syncObjectReader: SyncObjectReader,
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

        val sourceObject = instruction.objectIdInSource?.let { syncObjectReader.getSyncObject(it) }
        val targetObject = instruction.objectIdInTarget?.let { syncObjectReader.getSyncObject(it) }

        when(instruction.operation) {
            SyncOperation6.COPY_FROM_SOURCE_TO_TARGET -> copyFromSourceToTarget(sourceObject)
            SyncOperation6.COPY_FROM_TARGET_TO_SOURCE -> copyFromTargetToSource(targetObject)

            SyncOperation6.COPY_FROM_SOURCE_TO_TARGET_WITH_BACKUP -> copyFromSourceToTargetWithBackup(sourceObject)
            SyncOperation6.COPY_FROM_TARGET_TO_SOURCE_WITH_BACKUP -> copyFromTargetToSourceWithBackup(targetObject)

            SyncOperation6.MUTUAL_RENAME_AND_COPY -> mutualRenameAndCopy(sourceObject!!, targetObject!!)

            SyncOperation6.DELETE_IN_SOURCE -> deleteInSource(sourceObject)
            SyncOperation6.DELETE_IN_TARGET -> deleteInTarget(targetObject)

            SyncOperation6.DELETE_IN_SOURCE_WITH_BACKUP -> deleteInSourceWithBackup(targetObject)
            SyncOperation6.DELETE_IN_TARGET_WITH_BACKUP -> deleteInTargetWithBackup(targetObject)
        }
    }

    private fun copyFromSourceToTargetWithBackup(sourceObject: SyncObject?) {
        copierWithBackup.copyFromSourceToTargetWithBackup(sourceObject!!)
    }

    private fun copyFromTargetToSourceWithBackup(targetObject: SyncObject?) {
        copierWithBackup.copyFromTargetToSourceWithBackup(targetObject!!)
    }

    private fun deleteInSourceWithBackup(targetObject: SyncObject?) {
        deleterWithBackup.deleteInSourceWithBackup(targetObject!!)
    }

    private fun deleteInTargetWithBackup(targetObject: SyncObject?) {
        deleterWithBackup.deleteInTargetWithBackup(targetObject!!)
    }

    private suspend fun copyFromSourceToTarget(sourceObject: SyncObject?) {
        copier.copyFromSourceToTarget(sourceObject!!, syncOptions.overwriteIfExists)
    }

    private suspend fun copyFromTargetToSource(targetObject: SyncObject?) {
        copier.copyFromTargetToSource(targetObject!!, syncOptions.overwriteIfExists)
    }


    // FIXME: удалять сначала файлы, потом каталоги
    private suspend fun deleteInSource(sourceObject: SyncObject?) {
        if (sourceObject!!.isDir) deleter.deleteEmptyDirInSource(sourceObject)
        else deleter.deleteFileInSource(sourceObject)
    }

    private suspend fun deleteInTarget(targetObject: SyncObject?) {
        if (targetObject!!.isDir) deleter.deleteEmptyDirInTarget(targetObject)
        else deleter.deleteFileInTarget(targetObject)
    }

    private fun mutualRenameAndCopy(sourceObject: SyncObject, targetObject: SyncObject) {
        mutualRenamerAndCopier.mutualRenameAndCopy(sourceObject, targetObject)
    }




    private val copier: SyncObjectCopier5 by lazy {
        syncObjectCopierAssistedFactory5.create(syncTask)
    }

    private val copierWithBackup: SyncObjectCopierWithBackup5 by lazy {
        syncObjectCopierWithBackupAssistedFactory5.create(syncTask, executionId)
    }

    private val chunkedCopier by lazy {
        syncObjectListChunkedCopierAssistedFactory5.create(
            syncTask = syncTask,
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
        scope: CoroutineScope): SyncInstructionExecutor
}