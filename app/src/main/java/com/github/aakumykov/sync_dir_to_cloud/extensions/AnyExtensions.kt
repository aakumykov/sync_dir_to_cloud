package com.github.aakumykov.sync_dir_to_cloud.extensions

fun Any.classNameWithHash(): String = this.javaClass.simpleName + "(${hashCode()})"