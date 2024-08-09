package com.github.aakumykov.sync_dir_to_cloud.extensions

fun Any.classNameWithHash(): String = this.javaClass.simpleName + "(${hashCode()})"

val Any.tag: String get() = javaClass.simpleName

val Any.executionId: String get() = hashCode().toString()