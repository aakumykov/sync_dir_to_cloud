package com.github.aakumykov.sync_dir_to_cloud.utils

import java.text.SimpleDateFormat
import java.util.*

@Deprecated("Нужен для отладки")
class CurrentDateTime {
    companion object {
        fun get(): String = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale("ru"))
                .format(Date())
    }
}