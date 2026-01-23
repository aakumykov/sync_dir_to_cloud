package com.github.aakumykov.sync_dir_to_cloud.utils

object BytesToHumanSizeFormatter {

    fun format(bytes: Long) = format(bytes.toDouble())

    fun format(bytes: Double) = when {
        bytes >= 1 shl 30 -> "%.3f GB".format(bytes / (1 shl 30))
        bytes >= 1 shl 20 -> "%.3f MB".format(bytes / (1 shl 20))
        bytes >= 1 shl 10 -> "%.0f kB".format(bytes / (1 shl 10))
        else -> "$bytes bytes"
    }
}