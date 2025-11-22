package com.github.aakumykov.sync_dir_to_cloud.extensions

val Throwable.errorMsg: String get() = message ?: javaClass.name

val Throwable.errorMsgExtended: String get() = message?.let { "$it (${javaClass.name})" } ?: javaClass.name