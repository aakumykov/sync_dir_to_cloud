package com.github.aakumykov.sync_dir_to_cloud.extensions

fun <T> List<T>.nullIfEmpty(): List<T>? {
    return if (isEmpty()) null
    else this
}