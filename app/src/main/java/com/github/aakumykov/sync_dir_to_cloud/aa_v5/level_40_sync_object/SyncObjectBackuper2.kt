package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.absolutePathOfSide
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * @param backupsDirPath Путь к каталогу, в который складывать бекапы
 * в текущем запуске.
 */
class SyncObjectBackuper2 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    @Throws(IllegalArgumentException::class)
    fun backupDir(syncObject: SyncObject) {
        if (syncObject.isFile) throw argumentIsNotADirException(syncObject)

        cloudWriterOf(syncObject.syncSide).apply {
            createDir(backupDirPath(syncObject), syncObject.relativeParentDirPath).also {
                createDir(it, syncObject.name)
                // В логике программы не компетенция
                // непосредственного "Бекапера" удалять каталоги,
                // потому что их бекап связан с бекапом содержащихся
                // в них файлов.
//                deleteDir(syncObject)
            }
        }
    }


    @Throws(IllegalArgumentException::class)
    suspend fun backupFileWithMove(syncObject: SyncObject) {
        prepareDirForFileBackup(syncObject).also { createdDirPath ->

            cloudWriterOf(syncObject.syncSide).moveFileOrEmptyDir(
                fromAbsolutePath = syncObject.absolutePathIn(syncTask),
                toAbsolutePath = combineFSPaths(createdDirPath, syncObject.name),
                true
            )
        }
    }


    @Throws(IllegalArgumentException::class)
    suspend fun backupFileWithCopy(syncObject: SyncObject) {
        prepareDirForFileBackup(syncObject).also { createdDirPath ->
            cloudWriterOf(syncObject.syncSide).copyFile(
                fromAbsolutePath = syncObject.absolutePathIn(syncTask),
                toAbsolutePath = createdDirPath,
                true
            )
        }
    }

    private fun backupDirPath(syncObject: SyncObject): String = when(syncObject.syncSide) {
        SyncSide.SOURCE -> syncTask.sourceExecutionBackupDirPath!!
        SyncSide.TARGET -> syncTask.targetExecutionBackupDirPath!!
    }


    // TODO: убрать возвращаемое значение
    private suspend fun prepareDirForFileBackup(
        syncObject: SyncObject,
//        block: suspend (String) -> Unit
    ): String
    {
        if (syncObject.isDir) throw argumentIsNotAFileException(syncObject)

        return cloudWriterOf(syncObject.syncSide).let {
            it.createDir(backupDirPath(syncObject), syncObject.relativeParentDirPath).also { createdDirPath ->
//                block.invoke(createdDirPath)
//                markDirAsBackupped(syncInstruction, createdDirPath)
            }
        }
    }

    private fun argumentIsNotADirException(syncObject: SyncObject): IllegalArgumentException {
        return IllegalArgumentException("Argument is not a dir: $syncObject")
    }

    private fun argumentIsNotAFileException(syncObject: SyncObject): IllegalArgumentException {
        return IllegalArgumentException("Argument is not a file: $syncObject")
    }

    /*private fun deleteDir(syncObject: SyncObject) {
        if (syncObject.isFile) throw argumentIsNotADirException(syncObject)
        val storagePath = syncTask.absolutePathOfSide(syncObject.syncSide)
        val dirAbsolutePath = syncObject.absolutePathIn(storagePath)
        val parentAbsolutePath = parentPathFor(dirAbsolutePath)
        cloudWriterOf(syncObject.syncSide).deleteDir(parentAbsolutePath, syncObject.name)
    }*/

    /*suspend fun backupDirInSource(syncObject: SyncObject) {
        sourceCloudWriter.createDir(backupsDirPath, syncObject.relativeParentDirPath).also {
            sourceCloudWriter.createDir(it, syncObject.name)

            // Найти объект с именем и relativeParentDirPath
            // из syncObject.relativeParentDirPath, по его id (и syncSide?)
            // найти Инструкцию, отметить её как исполненную.
            // Если parentDir == rootDir, то пропустить.
            val parentDirName = fileNameFromPath(syncObject.relativeParentDirPath)
            val relParentPathOfParentDir = parentPathFor(syncObject.relativeParentDirPath)
            val parentDirObjectId = syncObjectDBReader.getSyncObject(
                taskId = syncTask.id,
                syncSide = SyncSide.SOURCE,
                name = parentDirName,
                relativeParentDirPath = relParentPathOfParentDir,
            )?.id

            if (null != parentDirObjectId) {
                syncInstructionReader.getSyncInstructionsForObjectInSource(parentDirObjectId)
                    .filter {  }
            }
        }
    }*/

    private suspend fun markDirAsBackupped(syncInstruction: SyncInstruction,
                                           relativeDirPath: String) {

        /*syncInstructionUpdater.markAsProcessed(
            operation = syncInstruction.operation
            relativePath = syncInstruction.relativePath
        )*/
    }

    private fun cloudWriterOf(syncSide: SyncSide): CloudWriter = when(syncSide) {
        SyncSide.SOURCE -> sourceCloudWriter
        SyncSide.TARGET -> targetCloudWriter
    }


    private val sourceCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getSourceCloudWriter(syncTask)
    }

    private val targetCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getTargetCloudWriter(syncTask)
    }
}


@AssistedFactory
interface SyncObjectBackuper2AssistedFactory {
    fun create(syncTask: SyncTask): SyncObjectBackuper2
}