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

class FSItemDeleter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val fileDeleterAssistedFactory: FileDeleterAssistedFactory5,
    private val dirDeleterAssistedFactory: DirDeleterAssistedFactory5,
    private val syncObjectUpdater: SyncObjectUpdater,
) {
    suspend fun deleteItemInSource(syncObject: SyncObject) {
        if (syncObject.isDir) deleteEmptyDirInSource(syncObject)
        else deleteFileInSource(syncObject)

        syncObjectUpdater.markAsDeleted(syncObject.id)
    }

    suspend fun deleteItemInTarget(syncObject: SyncObject) {
        if (syncObject.isDir) deleteEmptyDirInTarget(syncObject)
        else deleteFileInTarget(syncObject)

        syncObjectUpdater.markAsDeleted(syncObject.id)
    }


    private suspend fun deleteFileInSource(syncObject: SyncObject) {
        fileDeleter.deleteFileInSource(
            relativeParentDirPath = syncObject.relativeParentDirPath,
            fileName = syncObject.name
        )
    }

    private suspend fun deleteFileInTarget(syncObject: SyncObject) {
        fileDeleter.deleteFileInTarget(
            relativeParentDirPath = syncObject.relativeParentDirPath,
            fileName = syncObject.name
        )
    }

    // FIXME: логика путей неправильная, но работает
    private suspend fun deleteEmptyDirInSource(syncObject: SyncObject) {
        dirDeleter.deleteEmptyDirInSource(
            basePath = syncTask.sourcePath!!,
            dirName = syncObject.relativePath
        )
    }

    // FIXME: логика путей неправильная, но работает
    private suspend fun deleteEmptyDirInTarget(syncObject: SyncObject) {
        dirDeleter.deleteEmptyDirInTarget(
            basePath = syncTask.targetPath!!,
            dirName = syncObject.relativePath
        )
    }


    private val fileDeleter: FileDeleter5 by lazy { fileDeleterAssistedFactory.create(syncTask) }
    private val dirDeleter: DirDeleter5 by lazy { dirDeleterAssistedFactory.create(syncTask) }
}


@AssistedFactory
interface ItemDeleterAssistedFactory5 {
    fun create(syncTask: SyncTask): FSItemDeleter5
}