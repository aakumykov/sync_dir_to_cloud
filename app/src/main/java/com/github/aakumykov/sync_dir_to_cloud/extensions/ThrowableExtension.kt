package com.github.aakumykov.sync_dir_to_cloud.extensions

val Throwable.errorMsg: String get() = message ?: javaClass.name