package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import androidx.room.util.dropFtsSyncTriggers
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list.SyncObjectListChunkedCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list.SyncObjectListChunkedCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class SyncTaskFilesCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val scope: CoroutineScope,
    private val syncOptions: SyncOptions,
    private val syncObjectListChunkedCopierAssistedFactory5: SyncObjectListChunkedCopierAssistedFactory5
) {


    private val syncObjectListChunkedCopier: SyncObjectListChunkedCopier5
        get() = syncObjectListChunkedCopierAssistedFactory5.create(
            syncTask = syncTask,
            chunkSize = syncOptions.chunkSize,
            scope = scope
        )
}


@AssistedFactory
interface SyncTaskFilesCopierAssistedFactory5 {
    fun create(scope: CoroutineScope): SyncTaskFilesCopier5
}