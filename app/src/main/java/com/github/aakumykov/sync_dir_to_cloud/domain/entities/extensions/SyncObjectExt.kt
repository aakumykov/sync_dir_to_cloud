package com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide

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

val SyncObject.notYetSynced: Boolean get() {
    return ExecutionState.SUCCESS == syncState ||
            ExecutionState.ERROR == syncState ||
            ExecutionState.RUNNING == syncState
}

val SyncObject.isUnchanged: Boolean get() = StateInStorage.UNCHANGED == stateInStorage

val SyncObject.isNew: Boolean get() = StateInStorage.NEW == stateInStorage

val SyncObject.isSource: Boolean get() = SyncSide.SOURCE == syncSide

val SyncObject.isModified: Boolean get() = (StateInStorage.MODIFIED == stateInStorage)

val SyncObject.isDeleted: Boolean get() = StateInStorage.DELETED == stateInStorage

/**
 * Возвращает значение newSize, если оно есть, иначе значение поля size.
 */
val SyncObject.actualSize: Long get() = this.newSize ?: this.size