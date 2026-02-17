package com.github.aakumykov.sync_dir_to_cloud.enums

enum class LogItemAbout {
    TASK,
    INSTRUCTION,
    FILE,
    UNKNOWN;

    companion object {
//        fun random() = LogItemAbout.entries.toTypedArray().random()
        val random: LogItemAbout get() = LogItemAbout.entries.toTypedArray().random()
    }
}