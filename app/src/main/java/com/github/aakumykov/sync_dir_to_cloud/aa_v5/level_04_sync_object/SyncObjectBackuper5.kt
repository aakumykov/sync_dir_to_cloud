package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectBackuper5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
) {
}


@AssistedFactory
interface SyncObjectBackuperAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectBackuper5
}