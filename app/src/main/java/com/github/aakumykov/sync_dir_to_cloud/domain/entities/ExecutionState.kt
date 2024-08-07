package com.github.aakumykov.sync_dir_to_cloud.domain.entities

// FIXME: перечисление - это не entity!
@Deprecated("--> OperationState")
enum class ExecutionState {
    NEVER,
    RUNNING,
    SUCCESS,
    ERROR
}