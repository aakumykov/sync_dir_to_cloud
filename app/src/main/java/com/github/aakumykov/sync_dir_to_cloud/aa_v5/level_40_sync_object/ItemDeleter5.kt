package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.DirDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.DirDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ItemDeleter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val fileDeleterAssistedFactory: FileDeleterAssistedFactory5,
    private val dirDeleterAssistedFactory: DirDeleterAssistedFactory5,
    private val syncObjectUpdater: SyncObjectUpdater,
) {
    suspend fun deleteItemInSource(syncObject: SyncObject) {
        if (syncObject.isDir) deleteDirInSource(syncObject)
        else deleteFileInSource(syncObject)

        syncObjectUpdater.markAsDeleted(syncObject.id)
    }

    suspend fun deleteItemInTarget(syncObject: SyncObject) {
        if (syncObject.isDir) deleteDirInTarget(syncObject)
        else deleteFileInTarget(syncObject)

        syncObjectUpdater.markAsDeleted(syncObject.id)
    }


    private suspend fun deleteFileInSource(syncObject: SyncObject) {
        fileDeleter.deleteFileInSource(
            syncTask.sourcePath!!,
            syncObject.relativePath
        )
    }

    private suspend fun deleteFileInTarget(syncObject: SyncObject) {
        fileDeleter.deleteFileInTarget(
            syncTask.targetPath!!,
            syncObject.relativePath
        )
    }

    private suspend fun deleteDirInSource(syncObject: SyncObject) {
        dirDeleter.deleteEmptyDirInSource(
            syncTask.sourcePath!!,
            syncObject.relativePath
        )
    }

    private suspend fun deleteDirInTarget(syncObject: SyncObject) {
        dirDeleter.deleteEmptyDirInTarget(
            syncTask.targetPath!!,
            syncObject.relativePath
        )
    }


    private val fileDeleter: FileDeleter5 by lazy { fileDeleterAssistedFactory.create(syncTask) }
    private val dirDeleter: DirDeleter5 by lazy { dirDeleterAssistedFactory.create(syncTask) }
}


@AssistedFactory
interface ItemDeleterAssistedFactory5 {
    fun create(syncTask: SyncTask): ItemDeleter5
}