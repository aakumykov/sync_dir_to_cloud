package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list.SyncObjectListChunkedCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class InstructionsProcessor5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncObjectListChunkedCopierAssistedFactory5: SyncObjectListChunkedCopierAssistedFactory5,

) {
}