package com.github.aakumykov.sync_dir_to_cloud.enums

enum class LogItemType {
    BUSY,
    SUCCESS,
    CANCELLED,
    ERROR;

    companion object {
        val random: LogItemType get() = LogItemType.entries.toTypedArray().random()

        fun toSummarizedType(list: List<LogItemType>): LogItemType {
            return if (list.contains(ERROR)) ERROR
            else if (list.contains(CANCELLED)) CANCELLED
            else if (list.contains(BUSY)) BUSY
            else SUCCESS
        }
    }
}