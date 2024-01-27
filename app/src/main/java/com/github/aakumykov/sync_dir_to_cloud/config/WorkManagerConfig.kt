package com.github.aakumykov.sync_dir_to_cloud.config

class WorkManagerConfig private constructor() {
    companion object {
        const val PERIODIC_WORK_ID_PREFIX = "PERIODIC-"
        const val MANUAL_WORK_ID_PREFIX = "MANUAL-"
        const val PROBE_WORK_ID_PREFIX = "PROBE-"
    }
}