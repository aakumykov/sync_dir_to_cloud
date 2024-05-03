package com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

// TODO: дать имя, точно отражающее функцию.
fun SyncObject.shiftTwoVersionParameters(modifiedFSItem: FSItem) {

    if (null != newSize)
        size = newSize!!

    if (null != newMTime)
        mTime = newMTime!!

    this.newSize = modifiedFSItem.size
    this.newMTime = modifiedFSItem.mTime
}