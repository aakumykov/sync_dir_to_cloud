package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class SyncObjectListCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val oneFileCopyingScope: CoroutineScope,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
) {
    suspend fun copyListFromSourceToTarget(list: List<SyncObject>, overwriteIfExists: Boolean) {
        list.map { oneObject ->
            oneFileCopyingScope.launch {
                syncObjectCopier.copyFromSourceToTarget(oneObject, overwriteIfExists)
            }
        }.joinAll()
    }


    suspend fun copyListFromTargetToSource(list: List<SyncObject>, overwriteIfExists: Boolean) {
        list.map { oneObject ->
            oneFileCopyingScope.launch {
                syncObjectCopier.copyFromTargetToSource(oneObject, overwriteIfExists)
            }
        }.joinAll()
    }


    private val syncObjectCopier: SyncObjectCopier5
        get() = syncObjectCopierAssistedFactory5.create(syncTask)


    companion object {
        val TAG: String = SyncObjectListCopier5::class.java.simpleName
    }
}


@AssistedFactory
interface SyncObjectListCopierAssistedFactory5 {
    fun create(syncTask: SyncTask, scope: CoroutineScope): SyncObjectListCopier5
}