package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * "Item" - совокупный объект: SyncObject + физические данные в хранилище.
 * Копировать его - значит произвести операцию с физическими данными, SyncObject-ами.
 */
class ItemCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val fileCopier5AssistedFactory: FileCopier5AssistedFactory,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    // TODO: передать бы эти два действия в специальный класс...
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectAdder: SyncObjectAdder,
){
    // TODO: разобраться, как overwriteIfExists сочетается с бекапом

    @Throws(Exception::class)
    suspend fun copyItemFromSourceToTarget(syncObject: SyncObject, overwriteIfExists: Boolean) {
        if (syncObject.isDir) createDirInTarget(syncObject)
        else copyFileFromSourceToTarget(syncObject, overwriteIfExists)

        markAsSyncedAndRegisterInNewStorage(syncObject, SyncSide.TARGET)
    }

    @Throws(Exception::class)
    suspend fun copyItemFromTargetToSource(syncObject: SyncObject, overwriteIfExists: Boolean) {
        if (syncObject.isDir) createDirInSource(syncObject)
        else copyFileFromTargetToSource(syncObject, overwriteIfExists)

        markAsSyncedAndRegisterInNewStorage(syncObject, SyncSide.SOURCE)
    }


    private suspend fun markAsSyncedAndRegisterInNewStorage(
        syncObject: SyncObject,
        newSyncSide: SyncSide
    ) {
        syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)

        val newSyncObject = SyncObject.createFromExisting(
            syncObject = syncObject,
            newExecutionId = executionId,
            newSyncSide = newSyncSide,
        )

        // Вот почему "регистрацию" объекта нужно выносить в отдельный класс:
        // он должен считываться из БД, чтобы ...
        syncObjectAdder.addSyncObject(newSyncObject)

        syncObjectStateChanger.markAsSuccessfullySynced(newSyncObject.id)
    }


    private suspend fun copyFileFromSourceToTarget(syncObject: SyncObject, overwriteIfExists: Boolean) {
        fileCopier.copyFileFromSourceToTarget(
            syncObject,
            syncObject.absolutePathIn(syncTask.targetPath!!)
        )
    }


    private suspend fun copyFileFromTargetToSource(syncObject: SyncObject, overwriteIfExists: Boolean) {
        fileCopier.copyFileFromTargetToSource(
            syncObject,
            syncObject.absolutePathIn(syncTask.sourcePath!!)
        )
    }


    @Throws(Exception::class)
    private suspend fun createDirInTarget(syncObject: SyncObject) {
        dirCreator.createDirInTarget(
            basePath = syncObject.basePathIn(syncTask.targetPath!!),
            dirName = syncObject.name
        )
    }


    @Throws(Exception::class)
    private suspend fun createDirInSource(syncObject: SyncObject) {
        dirCreator.createDirInSource(
            basePath = syncObject.basePathIn(syncTask.sourcePath!!),
            dirName = syncObject.name
        )
    }



    private val fileCopier: FileCopier5 by lazy { fileCopier5AssistedFactory.create(syncTask, executionId) }

    private val dirCreator: DirCreator5 by lazy { dirCreator5AssistedFactory.create(syncTask) }
}


@AssistedFactory
interface ItemCopierAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): ItemCopier5
}