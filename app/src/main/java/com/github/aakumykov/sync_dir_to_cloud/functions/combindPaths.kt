package com.github.aakumykov.sync_dir_to_cloud.functions

import java.io.File

fun combineFSPaths(parentPath: String, childPath: String): String {
    return File(parentPath, childPath).absolutePath
}