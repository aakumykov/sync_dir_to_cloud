package com.github.aakumykov.sync_dir_to_cloud.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

class CurrentDateTime {

    companion object {

        private const val FORMAT = "yyyy-MM-dd HH:mm:ss"

        fun get(): String = SimpleDateFormat(FORMAT, Locale.getDefault()).format(Date())

        fun format(timestampMillis: Long) = SimpleDateFormat(
            FORMAT,
            Locale.getDefault()
        ).format(Instant.ofEpochMilli(timestampMillis).epochSecond)
    }
}