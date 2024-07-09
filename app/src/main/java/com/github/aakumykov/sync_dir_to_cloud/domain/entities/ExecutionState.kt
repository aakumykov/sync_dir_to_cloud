package com.github.aakumykov.sync_dir_to_cloud.domain.entities

@Deprecated("--> OperationState")
enum class ExecutionState {
    NEVER,
    RUNNING,
    SUCCESS,
    ERROR
}