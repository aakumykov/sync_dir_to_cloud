package com.github.aakumykov.sync_dir_to_cloud.functions

fun allNotNull(vararg args:Any?, block: (() -> Unit)? = {}) {
    if (args.all { it != null }) block?.invoke()
}