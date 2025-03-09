package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuper5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuperAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectCopierWithBackup5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncOptions: SyncOptions,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
    private val syncObjectBackuperAssistedFactory5: SyncObjectBackuperAssistedFactory5,
) {
    suspend fun copyFromSourceToTargetWithBackup(syncObject: SyncObject) {
        backuper.backupInTarget(syncObject)
        copier.copyFromSourceToTarget(syncObject, syncOptions.overwriteIfExists)
    }

    suspend fun copyFromTargetToSourceWithBackup(syncObject: SyncObject) {
        backuper.backupInSource(syncObject)
        copier.copyFromTargetToSource(syncObject, syncOptions.overwriteIfExists)
    }

    private val copier: SyncObjectCopier5 by lazy {
        syncObjectCopierAssistedFactory5.create(syncTask, executionId)
    }

    private val backuper: SyncObjectBackuper5 by lazy {
        syncObjectBackuperAssistedFactory5.create(syncTask, executionId)
    }
}


@AssistedFactory
interface SyncObjectCopierWithBackupAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectCopierWithBackup5
}