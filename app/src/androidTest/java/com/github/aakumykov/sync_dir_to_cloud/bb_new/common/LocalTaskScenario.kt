package com.github.aakumykov.sync_dir_to_cloud.bb_new.common

abstract class LocalTaskScenario() : TaskScenario() {
    protected val authId: String get() = taskConfig.SOURCE_AUTH_ID
}