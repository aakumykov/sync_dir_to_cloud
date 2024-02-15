package com.github.aakumykov.sync_dir_to_cloud.utils

import java.text.SimpleDateFormat
import java.util.*

@Deprecated("Нужен для отладки")
class CurrentDateTime {

    companion object {

        private const val FORMAT = "yyyy-MM-dd HH:mm:ss"

        fun get(): String = SimpleDateFormat(FORMAT, Locale.getDefault()).format(Date())

        fun format(timestamp: Long) = SimpleDateFormat(FORMAT, Locale.getDefault()).format(timestamp)
    }
}