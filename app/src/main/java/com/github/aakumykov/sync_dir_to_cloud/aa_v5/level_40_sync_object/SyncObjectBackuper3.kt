package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.CustomRootFileHelper
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.CustomRootFileHelperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.SourceTargetFileHelperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter.FileDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

//
// Задача БекапераОбъектов - бекапить объекты (файлы и папки),
// что включает в себя не только действия с файлами,
// но и изменения (какие?) в БД (каких?).
//
class SyncObjectBackuper3 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
    private val customRootFileHelperAssistedFactory: CustomRootFileHelperAssistedFactory,
    private val fileDeleterAssistedFactory5: FileDeleterAssistedFactory5,
    private val sourceTargetFileHelperAssistedFactory: SourceTargetFileHelperAssistedFactory,
    private val syncObjectDBDeleter: SyncObjectDBDeleter,
) {
    suspend fun backupSyncObject(syncObject: SyncObject) {
        if (syncObject.isDir) backupDir(syncObject)
        else backupFile(syncObject)
    }


    //
    // Забекапить каталог значит:
    // 1) создать в каталоге бекапов одноимённый каталог с полным путём;
    // 2) удалить каталог в текущем месте;
    // 3) удалить из БД запись о том, что он был в "текущем месте".
    //
    private suspend fun backupDir(syncObject: SyncObject) {
        if (syncObject.isFile) throw IllegalArgumentException("SyncObject is not a dir: $syncObject")
        when(syncObject.syncSide) {
            SyncSide.SOURCE -> backupDirInSource(syncObject)
            SyncSide.TARGET -> backupDirInTarget(syncObject)
        }
    }


    private suspend fun backupDirInSource(syncObject: SyncObject) {
        sourceInBackupsFileHelper.createDir(syncObject.relativePath)
        fileDeleter.deleteFileInSource(syncObject.relativeParentDirPath, syncObject.name)
        syncObjectDBDeleter.deleteObjectWithId(syncObject.id)
    }


    private suspend fun backupDirInTarget(syncObject: SyncObject) {
        targetInBackupsFileHelper.createDir(syncObject.relativePath)
        fileDeleter.deleteFileInTarget(syncObject.relativeParentDirPath, syncObject.name)
        syncObjectDBDeleter.deleteObjectWithId(syncObject.id)
    }


    //
    // Забекапить файл значит:
    // 10) создать в каталоге бекапов родительский каталог для этого файла,
    // аналогичный исходному;
    // 20) переместить его в этот каталог;
    // 25)
    // 30) удалить из БД запись о том, что он был в "текущем месте".
    //
    private suspend fun backupFile(syncObject: SyncObject) {
        if (syncObject.isDir) throw IllegalArgumentException("SyncObject is not a file: $syncObject")
        when(syncObject.syncSide) {
            SyncSide.SOURCE -> backupFileInSource(syncObject)
            SyncSide.TARGET -> backupFileInTarget(syncObject)
        }
    }


    private suspend fun backupFileInSource(syncObject: SyncObject) {
        sourceInBackupsFileHelper.createDir(syncObject.relativeParentDirPath)
        sourceTargetFileHelper.moveFileInSource(
            syncObject.absolutePathIn(syncTask),
            combineFSPaths(syncTask.sourceExecutionBackupDirPath!!, syncObject.relativePath)
        )
        syncObjectDBDeleter.deleteObjectWithId(syncObject.id)
    }


    private suspend fun backupFileInTarget(syncObject: SyncObject) {
        targetInBackupsFileHelper.createDir(syncObject.relativeParentDirPath)
        sourceTargetFileHelper.moveFileInTarget(
            syncObject.absolutePathIn(syncTask),
            combineFSPaths(syncTask.targetExecutionBackupDirPath!!, syncObject.relativePath)
        )
        syncObjectDBDeleter.deleteObjectWithId(syncObject.id)
    }


    private val sourceInBackupsFileHelper: CustomRootFileHelper by lazy {
        customRootFileHelperAssistedFactory.create(
            syncTask.sourceExecutionBackupDirPath!!,
            cloudWriterGetter.getSourceCloudWriter(syncTask)
        )
    }


    private val targetInBackupsFileHelper: CustomRootFileHelper by lazy {
        customRootFileHelperAssistedFactory.create(
            syncTask.targetExecutionBackupDirPath!!,
            cloudWriterGetter.getTargetCloudWriter(syncTask)
        )
    }


    private val fileDeleter: FileDeleter5 by lazy {
        fileDeleterAssistedFactory5.create(syncTask)
    }


    private val sourceTargetFileHelper by lazy {
        sourceTargetFileHelperAssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface SyncObjectBackuper3AssistedFactory {
    fun create(syncTask: SyncTask): SyncObjectBackuper3
}