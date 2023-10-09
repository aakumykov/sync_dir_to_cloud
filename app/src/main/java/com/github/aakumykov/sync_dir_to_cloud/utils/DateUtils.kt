package com.github.aakumykov.sync_dir_to_cloud.utils

import android.content.Context
import java.text.SimpleDateFormat

class DateUtils private constructor(){
    companion object {
        fun formatTime(context: Context, format: String, date: Long): String =
            SimpleDateFormat("HH:mm", LocaleUtils.getCurrentLocale(context))
                .format(date)
    }
}