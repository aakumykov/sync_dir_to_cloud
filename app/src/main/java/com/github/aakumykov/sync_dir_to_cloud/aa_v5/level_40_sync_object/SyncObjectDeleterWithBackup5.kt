package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectDeleterWithBackup5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncObjectDeleterAssistedFactory5: SyncObjectDeleterAssistedFactory5,
    private val syncObjectBackuperAssistedFactory: SyncObjectBackuperAssistedFactory,
){
    fun deleteInSourceWithBackup(syncObject: SyncObject) {

    }

    fun deleteInTargetWithBackup(syncObject: SyncObject) {

    }

    private val deleter: SyncObjectDeleter5 by lazy {
        syncObjectDeleterAssistedFactory5.create(syncTask, executionId)
    }

    private val backuper: SyncObjectBackuper5 by lazy {
        syncObjectBackuperAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface SyncObjectDeleterWithBackupAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectDeleterWithBackup5
}