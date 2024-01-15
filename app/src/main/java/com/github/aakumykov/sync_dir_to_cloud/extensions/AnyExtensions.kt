package com.github.aakumykov.sync_dir_to_cloud.extensions

fun Any.tagWithHashCode(): String = this.javaClass.simpleName + "(${hashCode()})"