package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.config.DateTimeConfig.SYNC_LOG_VIEW_HOLDER_DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.Locale

@Deprecated("Убрать прямое использование")
fun formattedDateTime(timestamp: Long): String =
    formattedDateTime(timestamp,BackupConfig.BACKUP_DIR_DATE_TIME_FORMAT )


fun formattedDateTime(timestamp: Long, format: String): String = SimpleDateFormat(
    format,
    Locale.getDefault()
).format(timestamp)


fun syncLogFormattedDateTime(timestamp: Long): String {
    return formattedDateTime(timestamp, SYNC_LOG_VIEW_HOLDER_DATE_TIME_FORMAT)
}


val backupDirFormattedDateTime: String
    get() = formattedDateTime(currentTime, BackupConfig.BACKUP_DIR_DATE_TIME_FORMAT)
