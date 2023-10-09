package com.github.aakumykov.sync_dir_to_cloud.config

class SyncTaskExecutionConfig private constructor() {
    companion object {
        const val DEFAULT_EXECUTION_PERIOD_MINUTES: Long = 24 * 60
    }
}