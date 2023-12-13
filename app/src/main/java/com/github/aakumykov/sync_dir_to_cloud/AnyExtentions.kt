package com.github.aakumykov.sync_dir_to_cloud

import android.util.Log

fun Any.LogD(message: String) {
    Log.d(tag(), message)
}

fun Any.LogE(message: String) {
    Log.e(tag(), message)
}

fun Any.LogE(e: Exception) {

}

fun Any.tag(): String = this.javaClass.simpleName