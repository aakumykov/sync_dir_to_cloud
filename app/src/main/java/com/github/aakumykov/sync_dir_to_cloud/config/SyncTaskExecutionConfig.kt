package com.github.aakumykov.sync_dir_to_cloud.config

class SyncTaskExecutionConfig private constructor() {
    companion object {
        const val DEFAULT_EXECUTION_PERIOD_HOURS: Int = 0 // 0-23
        const val DEFAULT_EXECUTION_PERIOD_MINUTES: Int = 0 // 0-59
    }
}