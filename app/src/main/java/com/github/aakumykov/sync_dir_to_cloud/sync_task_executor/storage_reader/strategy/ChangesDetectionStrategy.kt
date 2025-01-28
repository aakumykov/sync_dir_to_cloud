package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

abstract class ChangesDetectionStrategy {

    abstract suspend fun detectItemModification(
        sourcePath: String,
        newFsItem: FSItem,
        existingSyncObject: SyncObject
    ): StateInStorage


    companion object {
        val SIZE_AND_MODIFICATION_TIME = SizeAndModificationTimeChangesDetectionStrategy()
    }
}