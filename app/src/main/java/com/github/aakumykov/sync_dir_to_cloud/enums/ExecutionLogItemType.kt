package com.github.aakumykov.sync_dir_to_cloud.enums

enum class ExecutionLogItemType {
    @Deprecated("Не START, а RUNNING") START,
    FINISH,
    ERROR
}