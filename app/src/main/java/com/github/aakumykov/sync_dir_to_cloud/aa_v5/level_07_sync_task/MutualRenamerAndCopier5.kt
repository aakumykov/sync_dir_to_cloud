package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectRenamer5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectRenamerAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MutualRenamerAndCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val renamerAssistedFactory5: SyncObjectRenamerAssistedFactory5,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
){
    fun mutualRenameAndCopy(sourceObject: SyncObject, targetObject: SyncObject) {

    }


    private val renamer: SyncObjectRenamer5 by lazy {
        renamerAssistedFactory5.create(syncTask)
    }

    private val copier: SyncObjectCopier5 by lazy {
        syncObjectCopierAssistedFactory5.create(syncTask)
    }
}


@AssistedFactory
interface MutualRenamerAndCopierAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): MutualRenamerAndCopier5
}