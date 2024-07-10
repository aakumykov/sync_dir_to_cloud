package com.github.aakumykov.sync_dir_to_cloud.utils

import android.os.Build
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.BuildConfig

/**
 * Назначение класса - добавлять к меткам Log-сообщений общий префикс,
 * для удобства их отфильтровывания в системном журнале.
 */
object MyLogger {

    private val TAG_PREFIX: String = BuildConfig.APPLICATION_ID.split(".").last()

    fun d(tag: String, message: String) {
        Log.d(fullTag(tag), message)
    }

    fun i(tag: String, message: String) {
        Log.i(fullTag(tag), message)
    }

    fun w(tag: String, message: String) {
        Log.w(fullTag(tag), message)
    }

    fun e(tag: String, message: String) {
        Log.e(fullTag(tag), message)
    }

    fun e(tag: String, message: String, t: Throwable) {
        Log.e(fullTag(tag), message, t)
    }

    private fun fullTag(tag: String): String {
//        return if (isEmulator()) "$TAG_PREFIX, $tag" else tag
        return tag
    }
}