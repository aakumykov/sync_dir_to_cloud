package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MutualRenamerAndCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncOptions: SyncOptions,
    private val renamerAssistedFactory5: SyncObjectRenamerAssistedFactory5,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
    private val syncObjectUpdater: SyncObjectUpdater,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectRegistratorAssistedFactory: SyncObjectRegistratorAssistedFactory,
    ){
    suspend fun mutualRenameAndCopy(sourceObject: SyncObject, targetObject: SyncObject) {

        val newSourceObjectName = renamer.renameCollisionInSource(sourceObject)
        val newTargetObjectName = renamer.renameCollisionInTarget(sourceObject)

        syncObjectUpdater.updateName(sourceObject.id, newSourceObjectName)
        syncObjectUpdater.updateName(targetObject.id, newTargetObjectName)

        val newSourceObject = syncObjectReader.getSyncObject(sourceObject.id)
        val newTargetObject = syncObjectReader.getSyncObject(targetObject.id)

        // FIXME: избавиться от "!!"
        copier.copyFromSourceToTarget(newSourceObject!!, syncOptions.overwriteIfExists)
        copier.copyFromTargetToSource(newTargetObject!!, syncOptions.overwriteIfExists)

        syncObjectRegistrator.registerNewObjectInSource(newTargetObject)
        syncObjectRegistrator.registerNewObjectInTarget(newSourceObject)
    }


    private val renamer: SyncObjectRenamer5 by lazy {
        renamerAssistedFactory5.create(syncTask)
    }

    private val copier: SyncObjectCopier5 by lazy {
        syncObjectCopierAssistedFactory5.create(syncTask, executionId)
    }

    private val syncObjectRegistrator: SyncObjectRegistrator
        get() = syncObjectRegistratorAssistedFactory.create(syncTask, executionId)
}


@AssistedFactory
interface MutualRenamerAndCopierAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): MutualRenamerAndCopier5
}