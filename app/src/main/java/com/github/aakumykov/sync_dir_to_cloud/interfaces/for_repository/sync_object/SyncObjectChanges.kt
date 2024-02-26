package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

class SyncObjectChanges private constructor(
    val objectId: String,
    val fileName: String,
    val mTime: Long,
    val size: Long,
    val modificationState: ModificationState
) {
    companion object {
        fun create(objectId: String,
                   fsItem: FSItem,
                   modificationState: ModificationState
        ): SyncObjectChanges = SyncObjectChanges(
            objectId,
            fsItem.name,
            fsItem.mTime,
            fsItem.size,
            modificationState
        )

        fun createAsNew(syncObject: SyncObject): SyncObjectChanges {

            if (ModificationState.NEW != syncObject.modificationState)
                throw IllegalArgumentException("SyncObject must has 'NEW' modification state, existing is '${syncObject.modificationState}'")

            return SyncObjectChanges(
                syncObject.id,
                syncObject.name,
                syncObject.mTime,
                syncObject.size,
                syncObject.modificationState
            )
        }
    }
}