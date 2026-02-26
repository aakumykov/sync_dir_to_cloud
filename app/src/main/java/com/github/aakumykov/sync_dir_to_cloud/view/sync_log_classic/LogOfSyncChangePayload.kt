package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

class LogOfSyncChangePayload {

    private var _isChanged: Boolean = false

    val isChanged get() = _isChanged

    private fun markChanged() {
        _isChanged = true
    }

    var fistName: String? = null
        set(value) {
            markChanged()
            field = value
        }

    var lastName: String? = null
        set(value) {
            markChanged()
            field = value
        }
}
