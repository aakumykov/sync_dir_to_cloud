package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInSourceItemsProcessor @AssistedInject constructor(
    @Assisted syncTask: SyncTask,
    private val syncObjectCopier5AssistedFactory: SyncObjectCopierAssistedFactory5,
){
    suspend fun process(list: Iterable<SyncObject>) {
        createDirsInTarget(list.filter { it.isDir })
        copyFilesFromSourceToTarget(list.filter { it.isFile })
    }

    private suspend fun createDirsInTarget(dirObjectList: Iterable<SyncObject>) {
        dirObjectList.forEach {
            syncObjectCopier5.copyFromSourceToTarget(it, true)
        }
    }

    private suspend fun copyFilesFromSourceToTarget(fileObjectList: Iterable<SyncObject>) {
        fileObjectList.forEach { syncObjectCopier5.copyFromSourceToTarget(it, true) }
    }

    private val syncObjectCopier5: SyncObjectCopier5 by lazy {
        syncObjectCopier5AssistedFactory.create(syncTask)
    }
}



@AssistedFactory
interface OnlyInSourceItemsProcessorAssistedFactory {
    fun create(syncTask: SyncTask): OnlyInSourceItemsProcessor
}