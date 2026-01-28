package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.DirDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.DirDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Deprecated("FIXME: 'deleter' означает и интерфейс БД, и удаляльщик файлов")
class SyncObjectDeleter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val fileOperationLogger2AssistedFactory: FileOperationLogger2AssistedFactory,
    private val fileDeleterAssistedFactory: FileDeleterAssistedFactory5,
    private val dirDeleterAssistedFactory: DirDeleterAssistedFactory5,
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
            syncObject.basePathIn(syncTask.sourcePath!!),
            syncObject.name
        )
    }

    @Throws(Exception::class)
    suspend fun deleteFileInTarget(syncObject: SyncObject) {
        throwBadArgumentExceptionIfNotAFile(syncObject)
        fileDeleter.deleteFileInTarget(
            syncObject.basePathIn(syncTask.targetPath!!),
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

    private val fileOperationLogger by lazy {
        fileOperationLogger2AssistedFactory.create(syncTask.id, executionId)
    }

    private val dirDeleter: DirDeleter5 by lazy {
        dirDeleterAssistedFactory.create(syncTask)
    }

    private val fileDeleter: FileDeleter5 by lazy {
        fileDeleterAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface SyncObjectDeleterAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectDeleter5
}