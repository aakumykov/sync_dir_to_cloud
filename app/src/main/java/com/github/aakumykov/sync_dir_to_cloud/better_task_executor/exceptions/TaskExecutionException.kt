package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions

sealed class TaskExecutionException(errorMsg: String) : Exception(errorMsg) {

    sealed class CriticalException(errorMsg: String) : TaskExecutionException(errorMsg) {
        class TaskNotFoundException(errorMsg: String) : CriticalException(errorMsg)
        class SourceReadingException(errorMsg: String) : CriticalException(errorMsg)
        class BackingUpException(errorMsg: String) : CriticalException(errorMsg)
    }

    sealed class NonCriticalException(errorMsg: String) : TaskExecutionException(errorMsg) {
        class CreateDirException(errorMsg: String) : TaskExecutionException(errorMsg)
        class FileCopyException(errorMsg: String) : TaskExecutionException(errorMsg)
    }
}