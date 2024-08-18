package com.github.aakumykov.sync_dir_to_cloud.extensions

fun Float.round(decimalCount: Int): Float {
    val n: Float = decimalCount * 1f
    return Math.round(this * n) / n
}