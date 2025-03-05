package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInTargetItemsProcessor @AssistedInject constructor(
    @Assisted syncTask: SyncTask,
    private val syncObjectCopier5AssistedFactory: SyncObjectCopierAssistedFactory5,
){
    suspend fun process(list: Iterable<SyncObject>, syncMode: SyncMode) {
        if (SyncMode.MIRROR == syncMode) {
            createDirsInSource(list.filter { it.isDir })
            copyFilesFromTargetToSource(list.filter { it.isFile })
        }
    }

    private suspend fun createDirsInSource(dirObjectList: Iterable<SyncObject>) {
        dirObjectList.forEach { syncObjectCopier5.copyFromTargetToSource(it, true) }
    }

    private suspend fun copyFilesFromTargetToSource(fileObjectList: Iterable<SyncObject>) {
        fileObjectList.forEach { syncObjectCopier5.copyFromTargetToSource(it, true) }
    }


    private val syncObjectCopier5: SyncObjectCopier5 by lazy {
        syncObjectCopier5AssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface OnlyInTargetItemsProcessorAssistedFactory {
    fun create(syncTask: SyncTask): OnlyInTargetItemsProcessor
}