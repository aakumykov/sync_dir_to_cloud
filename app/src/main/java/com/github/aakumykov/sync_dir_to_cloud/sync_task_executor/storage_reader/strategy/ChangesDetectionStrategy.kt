package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import javax.inject.Inject

abstract class ChangesDetectionStrategy {

    abstract suspend fun detectItemModification(
        sourcePath: String,
        newFsItem: FSItem,
        existingSyncObject: SyncObject
    ): StateInSource


    class SizeAndModificationTime @Inject constructor() : ChangesDetectionStrategy() {

        override suspend fun detectItemModification(
            sourcePath: String,
            newFsItem: FSItem,
            existingSyncObject: SyncObject
        ): StateInSource {

            if (existingSyncObject.isDir)
                return StateInSource.UNCHANGED

            if (existingSyncObject.size == newFsItem.size) {
                if (existingSyncObject.mTime == newFsItem.mTime)
                    return StateInSource.UNCHANGED
            }

            return StateInSource.MODIFIED
        }
    }

    companion object {
        val SIZE_AND_MODIFICATION_TIME = SizeAndModificationTime()
    }
}