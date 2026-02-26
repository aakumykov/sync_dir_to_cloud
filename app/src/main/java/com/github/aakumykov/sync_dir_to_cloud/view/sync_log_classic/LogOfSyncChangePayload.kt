package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

class LogOfSyncChangePayload() {

    private var _isChanged: Boolean = false
    val isChanged get() = _isChanged

    private fun markChanged() { _isChanged = true }

    var isActiveFileOperation: Boolean = false
        set(value) { field = value; markChanged() }

    var logItemType: LogItemType? = null
        set(value) { field = value; markChanged() }

    var text: String? = null
        set(value) { field = value; markChanged() }

    var subText: String? = null
        set(value) { field = value; markChanged() }

    var progress: Float? = null
        set(value) { field = value; markChanged() }
}