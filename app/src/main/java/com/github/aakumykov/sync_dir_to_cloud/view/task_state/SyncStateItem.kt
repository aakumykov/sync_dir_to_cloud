package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

class SyncStateItem {

    companion object {

        fun makeTitle(syncObject: SyncObject): String = with(syncObject) {
            "$name (${modificationState} / ${executionState})"
        }
    }
}
