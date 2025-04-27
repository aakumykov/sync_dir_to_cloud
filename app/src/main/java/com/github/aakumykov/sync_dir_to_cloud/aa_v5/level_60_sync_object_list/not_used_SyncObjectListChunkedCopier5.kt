package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SyncObjectListChunkedCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val chunkSize: Int,
    @Assisted private val scope: CoroutineScope,
    private val syncObjectListCopierAssistedFactory5: SyncObjectListCopierAssistedFactory5,
) {
    // S --> T
    suspend fun copySyncObjectListFromSourceToTargetByChunksInScope(
        list: List<SyncObject>,
        overwriteIfExists: Boolean
    ): Job {
        return scope.launch {
            copySyncObjectListFromSourceToTargetByChunks(list, overwriteIfExists)
        }
    }

    // S <-- T
    suspend fun copySyncObjectListFromTargetToSourceByChunksInScope(
        list: List<SyncObject>,
        overwriteIfExists: Boolean
    ): Job {
        return scope.launch {
            copySyncObjectListFromTargetToSourceByChunks(list, overwriteIfExists)
        }
    }


    // S --> T
    suspend fun copySyncObjectListFromSourceToTargetByChunks(list: List<SyncObject>, overwriteIfExists: Boolean) {
        list.chunked(chunkSize).forEach { chunk ->
            syncObjectListCopier.copyListFromSourceToTarget(chunk, overwriteIfExists)
        }
    }

    // S <-- T
    suspend fun copySyncObjectListFromTargetToSourceByChunks(list: List<SyncObject>, overwriteIfExists: Boolean) {
        list.chunked(chunkSize).forEach { chunk ->
            syncObjectListCopier.copyListFromTargetToSource(chunk, overwriteIfExists)
        }
    }


    private val syncObjectListCopier: SyncObjectListCopier5
        get() = syncObjectListCopierAssistedFactory5.create(syncTask, executionId, scope)
}


@AssistedFactory
interface SyncObjectListChunkedCopierAssistedFactory5 {
    fun create(syncTask: SyncTask,
               executionId: String,
               chunkSize: Int,
               scope: CoroutineScope): SyncObjectListChunkedCopier5
}