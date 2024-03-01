package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import javax.inject.Inject

abstract class ChangesDetectionStrategy {

    abstract suspend fun detectItemModification(sourcePath: String, newFsItem: FSItem, existingSyncObject: SyncObject): ModificationState


    class SizeAndModificationTime @Inject constructor() : ChangesDetectionStrategy() {

        override suspend fun detectItemModification(sourcePath: String, newFsItem: FSItem, existingSyncObject: SyncObject): ModificationState {

            if (existingSyncObject.isDir)
                return ModificationState.UNCHANGED

            if (existingSyncObject.size == newFsItem.size) {
                if (existingSyncObject.mTime == newFsItem.mTime)
                    return ModificationState.UNCHANGED
            }

            return ModificationState.MODIFIED
        }
    }
}