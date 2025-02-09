package com.github.aakumykov.sync_dir_to_cloud.extensions

fun String.stripMultiSlash(): String {
    return this.replace(Regex("/+"),"/")
}

fun String.aquote(): String = "«$this»"