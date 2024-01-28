package com.github.aakumykov.sync_dir_to_cloud.config

import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

class WorkManagerConfig private constructor() {
    companion object {
        const val PERIODIC_FLEX_INTERVAL: Long = PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS
        val PERIODIC_FLEX_UNITS: TimeUnit = TimeUnit.MILLISECONDS

        const val PERIODIC_WORK_ID_PREFIX = "PERIODIC-"
        const val MANUAL_WORK_ID_PREFIX = "MANUAL-"
        const val PROBE_WORK_ID_PREFIX = "PROBE-"
    }
}