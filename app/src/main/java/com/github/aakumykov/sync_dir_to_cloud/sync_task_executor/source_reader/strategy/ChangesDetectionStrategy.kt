package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

abstract class ChangesDetectionStrategy {

    abstract fun isFileChanged(oldFSItem: FSItem, newFsItem: FSItem): Boolean

    class SizeAndModificationTime() : ChangesDetectionStrategy() {
        override fun isFileChanged(oldFSItem: FSItem, newFsItem: FSItem): Boolean {
            return when {
                (oldFSItem.mTime != newFsItem.mTime) -> true
                (oldFSItem.size != newFsItem.size) -> true
                else -> false
            }
        }
    }
}