package com.github.aakumykov.sync_dir_to_cloud.utils

import android.content.Context
import android.os.Build
import java.util.*

class LocaleUtils private constructor(){
    companion object {
        fun getCurrentLocale(context: Context): Locale {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0)
            } else {
                context.resources.configuration.locale
            }
        }
    }
}
