package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.extensions

import java.io.File

val File.dirIsEmpty: Boolean
    @Throws(NoSuchFileException::class, RuntimeException::class)
    get() {

        if (!exists()) throw NoSuchFileException(this)

        return when {
            (isDirectory) -> list().let {
                when (it) {
                    null -> throw RuntimeException("Error reading dir '${absolutePath}'")
                    else -> it.isEmpty()
                }
            }
            else -> 0L == length()
        }
    }
