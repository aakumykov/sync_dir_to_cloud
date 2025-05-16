package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import java.text.SimpleDateFormat
import java.util.Locale

fun formattedDateTime(timestamp: Long): String = SimpleDateFormat(
    BackupConfig.BACKUP_DIR_DATE_TIME_FORMAT,
    Locale.getDefault()
).format(timestamp)