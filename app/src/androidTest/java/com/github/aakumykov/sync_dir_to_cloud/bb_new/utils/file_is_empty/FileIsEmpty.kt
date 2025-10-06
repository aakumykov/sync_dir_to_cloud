package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty

import java.io.File
import java.io.FileNotFoundException

val File.isEmpty: Boolean
    @Throws(FileNotFoundException::class, RuntimeException::class)
    get() {
        if (!exists()) throw FileNotFoundException(this.absolutePath)

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
