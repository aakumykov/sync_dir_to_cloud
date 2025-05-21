package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuper2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.objectIdInSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackupInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncObjectBackuper2AssistedFactory: SyncObjectBackuper2AssistedFactory,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectDBDeleter: SyncObjectDBDeleter,
    // FIXME: довольно неоднозначно удалять файл здесь
    private val fileDeleterAssistedFactory5: FileDeleterAssistedFactory5,
){
    @Throws(IllegalArgumentException::class)
    suspend fun backupWithCopy(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        getObjectAndDeleteWithFileOnDone(syncInstruction, syncSide){ syncObject ->
            backupWithCopy(syncObject)
        }
    }

    suspend fun backupWithMove(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        getObjectAndDeleteWithFileOnDone(syncInstruction, syncSide){ syncObject ->
            backupWithMove(syncObject)
        }
    }

    private suspend fun getObjectAndDeleteWithFileOnDone(
        syncInstruction: SyncInstruction,
        syncSide: SyncSide,
        block: suspend (SyncObject) -> Unit
    ) {
        val objectId = syncInstruction.objectIdInSide(syncSide)
            ?: throw IllegalArgumentException("Where is no '$syncSide' object id in instruction: $syncInstruction")

        val syncObject = syncObjectDBReader.getSyncObject(objectId)
            ?: throw RuntimeException("There is no object with id='$objectId'")

        block.invoke(syncObject)

        syncObjectDBDeleter.deleteObjectWithId(syncObject.id)

        deleteFromStorageIfDir(syncObject)
    }

    private suspend fun deleteFromStorageIfDir(syncObject: SyncObject) {
        if (syncObject.isDir) {
            when(syncObject.syncSide) {
                SyncSide.SOURCE -> fileDeleter.deleteFileInSource(syncObject.relativeParentDirPath, syncObject.name)
                SyncSide.TARGET -> fileDeleter.deleteFileInTarget(syncObject.relativeParentDirPath, syncObject.name)
            }
        }
    }


    private suspend fun backupWithMove(syncObject: SyncObject) {
        if (syncObject.isDir) backupDir(syncObject)
        else syncObjectBackuper2.backupFileWithMove(syncObject)
    }


    private suspend fun backupWithCopy(syncObject: SyncObject) {
        if (syncObject.isDir) backupDir(syncObject)
        else syncObjectBackuper2.backupFileWithCopy(syncObject)
    }


    private fun backupDir(syncObject: SyncObject) {
        syncObjectBackuper2.backupDir(syncObject)
    }


    // TODO: реакция на оцуцтвие
    private suspend fun getObject(syncInstruction: SyncInstruction, syncSide: SyncSide): SyncObject {
        return syncObjectDBReader.getSyncObject(syncInstruction.objectIdInSide(syncSide)!!)!!
    }


    private val syncObjectBackuper2 by lazy {
        syncObjectBackuper2AssistedFactory.create(syncTask)
    }

    private val fileDeleter: FileDeleter5 by lazy {
        fileDeleterAssistedFactory5.create(syncTask)
    }
}


@AssistedFactory
interface BackupInstructionExecutorAssistedFactory {
    fun create(syncTask: SyncTask): BackupInstructionExecutor
}