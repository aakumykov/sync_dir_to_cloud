package com.github.aakumykov.sync_dir_to_cloud.utils

import android.content.Context
import java.text.DecimalFormat
import kotlin.math.log10

class FileSizeHelper {
    companion object {

        fun bytes2size(context: Context, bytesCount: Long, shortForm: Boolean = false): String {
            return if (shortForm) bytes2sizeShort(context, bytesCount)
            else bytes2sizeLong(context, bytesCount)
        }


        fun fileSize(bytesCount: Long):String {
            if (bytesCount <= 0) return "0"
            val units = arrayOf("B", "kB", "MB", "GB", "TB", "PB")
            val digitGroups = (log10(bytesCount.toDouble()) / log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(bytesCount / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
        }


        private fun bytes2sizeShort(context: Context,  bytesCount: Long): String {
            return android.text.format.Formatter.formatShortFileSize(context, bytesCount)
        }

        private fun bytes2sizeLong(context: Context,  bytesCount: Long): String {
            return android.text.format.Formatter.formatFileSize(context, bytesCount)
        }
    }
}