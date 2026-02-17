package com.github.aakumykov.sync_dir_to_cloud.enums

enum class LogItemType {
    BUSY,
    SUCCESS,
    CANCELLED,
    ERROR;

    companion object {
        val random: LogItemType get() = LogItemType.entries.toTypedArray().random()
    }
}