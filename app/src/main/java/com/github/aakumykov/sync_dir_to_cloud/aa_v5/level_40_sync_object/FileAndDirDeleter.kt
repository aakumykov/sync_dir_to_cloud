package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.DirDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.DirDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Deprecated("FIXME: 'deleter' означает и интерфейс БД, и удаляльщик файлов")
class FileAndDirDeleter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val fileDeleterAssistedFactory: FileDeleterAssistedFactory,
    private val dirDeleterAssistedFactory: DirDeleterAssistedFactory,
) {
    @Throws(Exception::class)
    suspend fun deleteEmptyDirInSource(syncObject: SyncObject) {
        throwBadArgumentExceptionIfNotADir(syncObject)
        dirDeleter.deleteEmptyDirInSource(
            syncObject.basePathIn(syncTask.sourcePath!!),
            syncObject.name
        )
    }

    @Throws(Exception::class)
    suspend fun deleteEmptyDirInTarget(syncObject: SyncObject) {
        val path = syncObject.absolutePathIn(syncTask.targetPath!!)

        throwBadArgumentExceptionIfNotADir(syncObject)
        dirDeleter.deleteEmptyDirInTarget(
            syncObject.basePathIn(syncTask.targetPath!!),
            syncObject.name
        )
    }


    @Throws(Exception::class)
    suspend fun deleteFileInSource(syncObject: SyncObject) {
        throwBadArgumentExceptionIfNotAFile(syncObject)
        fileDeleter.deleteFileInSource(
            syncObject.relativeParentDirPath,
            syncObject.name
        )
    }

    @Throws(Exception::class)
    suspend fun deleteFileInTarget(syncObject: SyncObject) {
        throwBadArgumentExceptionIfNotAFile(syncObject)
        fileDeleter.deleteFileInTarget(
            syncObject.relativeParentDirPath,
            syncObject.name
        )
    }


    @Throws(IllegalArgumentException::class)
    private fun throwBadArgumentExceptionIfNotADir(syncObject: SyncObject) {
        if (syncObject.isFile)
            throw IllegalArgumentException("SyncObject is not a dir object (id: ${syncObject.id}, name:${syncObject.name}).")
    }

    @Throws(IllegalArgumentException::class)
    private fun throwBadArgumentExceptionIfNotAFile(syncObject: SyncObject) {
        if (syncObject.isDir)
            throw IllegalArgumentException("SyncObject is not a file object (id: ${syncObject.id}, name:${syncObject.name}).")
    }

    private val dirDeleter: DirDeleter by lazy {
        dirDeleterAssistedFactory.create(syncTask)
    }

    private val fileDeleter: FileDeleter by lazy {
        fileDeleterAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface FileAndDirDeleterAssistedFactory {
    fun create(syncTask: SyncTask): FileAndDirDeleter
}