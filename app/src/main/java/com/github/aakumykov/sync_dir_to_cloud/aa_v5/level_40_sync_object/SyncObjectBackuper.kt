package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper.FileAndDirBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectBackuper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectDBDeleter: SyncObjectDBDeleter,
    private val fileAndDirBackuperAssistedFactory: FileAndDirBackuperAssistedFactory,
) {
    suspend fun backupWithCopyInSource(syncInstruction: SyncInstruction) {
        val absolutePath = getItemAbsolutePath(syncInstruction, SyncSide.SOURCE)
        if (syncInstruction.isDir) sourceBackuper.backupDir(absolutePath)
        else sourceBackuper.backupFileByCopy(absolutePath)
    }

    suspend fun backupWithCopyInTarget(syncInstruction: SyncInstruction) {
        val absolutePath = getItemAbsolutePath(syncInstruction, SyncSide.TARGET)
        if (syncInstruction.isDir) targetBackuper.backupDir(absolutePath)
        else targetBackuper.backupFileByCopy(absolutePath)
    }


    suspend fun backupWithMoveInSource(syncInstruction: SyncInstruction) {
        val absolutePath = getItemAbsolutePath(syncInstruction, SyncSide.SOURCE)

        if (syncInstruction.isDir) sourceBackuper.backupDir(absolutePath)
        else sourceBackuper.backupFileByMove(absolutePath, SyncSide.SOURCE)

        syncObjectDBDeleter.deleteObjectWithId(syncInstruction.objectIdInSource!!)
    }

    suspend fun backupWithMoveInTarget(syncInstruction: SyncInstruction) {
        val absolutePath = getItemAbsolutePath(syncInstruction, SyncSide.TARGET)

        if (syncInstruction.isDir) targetBackuper.backupDir(absolutePath)
        else targetBackuper.backupFileByMove(absolutePath, SyncSide.TARGET)

        syncObjectDBDeleter.deleteObjectWithId(syncInstruction.objectIdInTarget!!)
    }


    @Throws(RuntimeException::class)
    private suspend fun getItemAbsolutePath(syncInstruction: SyncInstruction, syncSide: SyncSide): String {

        val objectId = when(syncSide) {
            SyncSide.SOURCE -> syncInstruction.objectIdInSource!!
            SyncSide.TARGET -> syncInstruction.objectIdInTarget!!
        }

        val syncObject = syncObjectDBReader.getSyncObject(objectId)
            ?: run {
                throw RuntimeException("SyncObject with id '$objectId' in '$syncSide' location not found.")
            }

        return syncObject.absolutePathIn(when(syncSide){
            SyncSide.SOURCE -> syncTask.sourcePath!!
            SyncSide.TARGET -> syncTask.targetPath!!
        })
    }



    private val sourceBackuper by lazy {
        fileAndDirBackuperAssistedFactory.create(syncTask, SyncSide.SOURCE)
    }

    private val targetBackuper by lazy {
        fileAndDirBackuperAssistedFactory.create(syncTask, SyncSide.TARGET)
    }
}


@AssistedFactory
interface SyncObjectBackuperAssistedFactory {
    fun create(syncTask: SyncTask): SyncObjectBackuper
}