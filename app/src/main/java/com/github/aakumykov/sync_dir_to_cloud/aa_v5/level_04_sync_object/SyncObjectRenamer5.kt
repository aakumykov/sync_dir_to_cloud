package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectRenamer5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
}


@AssistedFactory
interface SyncObjectRenamerAssistedFactory5 {
    fun create(syncTask: SyncTask): SyncObjectRenamer5
}