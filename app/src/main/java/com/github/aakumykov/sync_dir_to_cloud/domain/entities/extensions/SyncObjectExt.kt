package com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInSource
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

val SyncObject.isTargetReadingOk: Boolean get() = ExecutionState.SUCCESS == targetReadingState

val SyncObject.isFile get() = !isDir

val SyncObject.notExistsInTarget get() = !isExistsInTarget

val SyncObject.isSuccessSynced: Boolean get() = ExecutionState.SUCCESS == syncState

val SyncObject.isNeverSynced: Boolean get() = ExecutionState.NEVER == syncState

val SyncObject.isUnchanged: Boolean get() = StateInSource.UNCHANGED == stateInSource

val SyncObject.isNew: Boolean get() = StateInSource.NEW == stateInSource

val SyncObject.isModified: Boolean get() = (StateInSource.MODIFIED == stateInSource)

val SyncObject.isDeleted: Boolean get() = StateInSource.DELETED == stateInSource

val SyncObject.actualSize: Long get() = this.newSize ?: this.size